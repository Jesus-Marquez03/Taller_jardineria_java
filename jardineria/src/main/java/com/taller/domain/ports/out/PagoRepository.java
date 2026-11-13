package com.taller.domain.ports.out;

import com.taller.domain.model.Pago;
import com.taller.domain.model.PagoId;
import java.util.List;
import java.util.Optional;

/** Puerto de salida para pagos, identificados por la clave compuesta {@link PagoId}. */
public interface PagoRepository {
    /** Inserta un pago. */
    void guardar(Pago pago);
    /** @return todos los pagos */
    List<Pago> listarTodos();
    /** @return el pago encontrado o vacío */
    Optional<Pago> buscarPorId(PagoId id);
    /** @return si se actualizó una fila */
    boolean actualizar(Pago pago);
    /** @return si se eliminó una fila */
    boolean eliminarPorId(PagoId id);
}
