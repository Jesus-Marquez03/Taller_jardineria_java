package com.taller.domain.ports.out;

import com.taller.domain.model.Pago;
import com.taller.domain.model.PagoId;
import java.util.List;
import java.util.Optional;

public interface PagoRepository {
    void guardar(Pago pago);
    List<Pago> listarTodos();
    Optional<Pago> buscarPorId(PagoId id);
    boolean actualizar(Pago pago);
    boolean eliminarPorId(PagoId id);
}
