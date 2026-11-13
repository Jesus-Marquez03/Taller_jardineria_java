package com.taller.domain.ports.out;

import com.taller.domain.model.Oficina;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida que define cómo la aplicación necesita persistir oficinas.
 *
 * <p>Una interfaz declara el contrato sin explicar la tecnología. Los servicios
 * dependen de este puerto y {@code OficinaJdbcRepository} lo implementa con
 * MySQL. Así se aplica inversión de dependencias: el negocio no depende de JDBC.</p>
 */
public interface OficinaRepository {
    /** Inserta una oficina nueva. */
    void guardar(Oficina oficina);
    /** @return todas las oficinas almacenadas */
    List<Oficina> listarTodos();
    /** @return la oficina encontrada o un {@link Optional} vacío */
    Optional<Oficina> buscarPorId(String codigoOficina);
    /** @return {@code true} si alguna fila fue modificada */
    boolean actualizar(Oficina oficina);
    /** @return {@code true} si alguna fila fue eliminada */
    boolean eliminarPorId(String codigoOficina);
}
