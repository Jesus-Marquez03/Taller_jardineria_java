package com.taller.application.ports.in;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada genérico para los casos de uso CRUD.
 *
 * <p>{@code T} es el tipo de entidad y {@code ID} el tipo de su clave. La
 * interfaz gráfica llama a este contrato a través de los servicios, sin hablar
 * directamente con JDBC. Esto mantiene separadas la presentación y el negocio.</p>
 */
public interface CrudUseCase<T, ID> {
    /** Guarda una entidad nueva después de aplicar las reglas del servicio. */
    void guardar(T entidad);
    /** @return todas las entidades disponibles */
    List<T> listarTodos();
    /** @return la entidad encontrada o un {@link Optional} vacío */
    Optional<T> buscarPorId(ID id);
    /** Modifica una entidad que ya existe. */
    void actualizar(T entidad);
    /** Elimina por identificador cuando las relaciones de la base lo permiten. */
    void eliminar(ID id);
}
