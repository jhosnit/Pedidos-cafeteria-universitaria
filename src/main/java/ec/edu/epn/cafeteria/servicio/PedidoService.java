package ec.edu.epn.cafeteria.servicio;

import ec.edu.epn.cafeteria.dao.PedidoDao;
import ec.edu.epn.cafeteria.dao.ProductoDao;
import ec.edu.epn.cafeteria.modelo.DetallePedido;
import ec.edu.epn.cafeteria.modelo.EstadoPedido;
import ec.edu.epn.cafeteria.modelo.ItemSeleccion;
import ec.edu.epn.cafeteria.modelo.Pedido;
import ec.edu.epn.cafeteria.modelo.Producto;
import ec.edu.epn.cafeteria.modelo.ReglaNegocioException;
import ec.edu.epn.cafeteria.modelo.Seleccion;

import java.util.ArrayList;
import java.util.List;

/**
 * Reglas del pedido: armar la seleccion, confirmar y consultar (HU-05 a HU-08).
 *
 * @since 1.0
 */
public class PedidoService {

    /** Intentos para encontrar un codigo libre antes de rendirse. */
    private static final int INTENTOS_CODIGO = 50;

    private final PedidoDao pedidoDao;
    private final ProductoDao productoDao;

    public PedidoService() {
        this(new PedidoDao(), new ProductoDao());
    }

    /**
     * @param pedidoDao   dao de pedidos
     * @param productoDao dao de productos, para revisar precio y disponibilidad vigentes
     */
    public PedidoService(PedidoDao pedidoDao, ProductoDao productoDao) {
        this.pedidoDao = pedidoDao;
        this.productoDao = productoDao;
    }

    /**
     * Agrega un producto a la seleccion despues de validar la cantidad.
     *
     * @param seleccion     seleccion guardada en la sesion
     * @param productoId    producto elegido
     * @param cantidadTexto cantidad tal como llega del formulario
     * @throws ReglaNegocioException si la cantidad no es valida o el producto no se puede pedir
     */
    public void agregar(Seleccion seleccion, int productoId, String cantidadTexto) {
        int cantidad = leerCantidad(cantidadTexto);
        Producto producto = productoDao.buscarPorId(productoId);
        if (producto == null) {
            throw new ReglaNegocioException("El producto no existe.");
        }
        seleccion.agregar(producto, cantidad);
    }

    /**
     * @param seleccion     seleccion guardada en la sesion
     * @param productoId    producto a modificar
     * @param cantidadTexto nueva cantidad
     * @throws ReglaNegocioException si la cantidad no es valida
     */
    public void cambiarCantidad(Seleccion seleccion, int productoId, String cantidadTexto) {
        seleccion.cambiarCantidad(productoId, leerCantidad(cantidadTexto));
    }

    /**
     * Confirma el pedido. Antes de guardar vuelve a revisar en la base que cada
     * producto siga disponible y con el mismo precio (CA-07.4, CA-07.5). Si algo
     * cambio, no crea el pedido y la seleccion se conserva.
     *
     * @param seleccion seleccion del cliente
     * @param clienteId id del cliente en sesion
     * @return el pedido creado, con su codigo de retiro
     * @throws ReglaNegocioException si la seleccion esta vacia, un producto ya no esta
     *                               disponible o algun precio cambio
     */
    public Pedido confirmar(Seleccion seleccion, int clienteId) {
        if (seleccion.isVacia()) {
            throw new ReglaNegocioException("Tu selección está vacía. Agrega al menos un producto.");
        }
        List<String> preciosCambiados = new ArrayList<>();
        for (ItemSeleccion item : seleccion.getItems()) {
            Producto actual = productoDao.buscarPorId(item.getProductoId());
            if (actual == null || !actual.isDisponible()) {
                throw new ReglaNegocioException("El producto " + item.getNombre()
                        + " ya no está disponible. Quítalo de tu selección para continuar.");
            }
            // compareTo y no equals: equals tambien compara la escala (2.5 no es igual a 2.50)
            if (actual.getPrecio().compareTo(item.getPrecio()) != 0) {
                preciosCambiados.add(item.getNombre());
                item.setPrecio(actual.getPrecio());
            }
        }
        if (!preciosCambiados.isEmpty()) {
            throw new ReglaNegocioException("Cambió el precio de: " + String.join(", ", preciosCambiados)
                    + ". El nuevo total es $" + seleccion.getTotal() + ". Revísalo y vuelve a confirmar.");
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(clienteId);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setImporteTotal(seleccion.getTotal());
        for (ItemSeleccion item : seleccion.getItems()) {
            pedido.getDetalles().add(new DetallePedido(item.getProductoId(), item.getNombre(),
                    item.getCantidad(), item.getPrecio()));
        }
        for (int i = 0; i < INTENTOS_CODIGO; i++) {
            pedido.setCodigoRetiro(CodigoRetiro.generar());
            if (pedidoDao.insertar(pedido)) {
                seleccion.vaciar();
                return pedido;
            }
        }
        // Con 67.600 codigos posibles esto solo pasaria si casi se agotan (SUP-21)
        throw new ReglaNegocioException("No se pudo generar un código de retiro. Intenta de nuevo.");
    }

    /**
     * @param clienteId id del cliente en sesion
     * @return sus pedidos, del mas reciente al mas antiguo
     */
    public List<Pedido> misPedidos(int clienteId) {
        return pedidoDao.listarPorCliente(clienteId);
    }

    /**
     * Detalle de un pedido solo si pertenece al cliente (CA-08.2).
     *
     * @param pedidoId  pedido solicitado
     * @param clienteId cliente en sesion
     * @return el pedido, o {@code null} si no existe o es de otra persona
     */
    public Pedido detalleDeCliente(int pedidoId, int clienteId) {
        Pedido pedido = pedidoDao.buscarPorId(pedidoId);
        return pedido != null && pedido.getClienteId() == clienteId ? pedido : null;
    }

    private int leerCantidad(String texto) {
        int cantidad = Validaciones.cantidad(texto);
        if (cantidad < 0) {
            throw new ReglaNegocioException("La cantidad debe ser un número entero entre 1 y "
                    + Seleccion.CANTIDAD_MAXIMA + ".");
        }
        return cantidad;
    }
}
