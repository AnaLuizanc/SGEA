/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseRepository<T extends Identifiable<ID>, ID> {

    protected final Map<ID, T> entities = new ConcurrentHashMap<>();
    private final String entityName;

    /**
     * Construtor para a classe base do repositório.
     * @param clazz A classe da entidade, usada para mensagens de erro.
     */
    protected BaseRepository(Class<T> clazz) {
        // Usa o nome simples da classe para mensagens de erro mais claras.
        this.entityName = clazz.getSimpleName();
    }

    /**
     * Salva (cria ou atualiza) uma entidade.
     * @param entity A entidade a ser salva.
     * @return A entidade salva.
     * @throws IllegalArgumentException se a entidade ou seu ID for nulo.
     */
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException(this.entityName + " não pode ser nulo(a).");
        }
        ID entityId = entity.getId();
        if (entityId == null) {
            // Para este projeto, os IDs (UUIDs) são gerados na construção da entidade.
            // Se o ID fosse gerado pelo banco, a lógica aqui seria diferente (ex: não verificar ID nulo na criação).
            throw new IllegalArgumentException(this.entityName + " ID não pode ser nulo para salvar.");
        }
        entities.put(entityId, entity);
        return entity;
    }

    /**
     * Encontra uma entidade pelo seu ID.
     * @param id O ID da entidade.
     * @return Um Optional contendo a entidade se encontrada, ou Optional.empty() caso contrário.
     */
    public Optional<T> findById(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entities.get(id));
    }

    /**
     * Retorna todas as entidades do repositório.
     * @return Uma lista de todas as entidades.
     */
    public List<T> findAll() {
        return new ArrayList<>(entities.values());
    }

    /**
     * Verifica se uma entidade com o ID fornecido existe.
     * @param id O ID da entidade.
     * @return true se existir, false caso contrário.
     */
    public boolean existsById(ID id) {
        return id != null && entities.containsKey(id);
    }

    /**
     * Deleta uma entidade.
     * @param entity A entidade a ser deletada.
     */
    public void delete(T entity) {
        if (entity != null && entity.getId() != null) {
            entities.remove(entity.getId());
        }
    }

    /**
     * Deleta uma entidade pelo seu ID.
     * @param id O ID da entidade a ser deletada.
     */
    public void deleteById(ID id) {
        if (id != null) {
            entities.remove(id);
        }
    }

    /**
     * Conta o número total de entidades no repositório.
     * @return O número de entidades.
     */
    public long count() {
        return entities.size();
    }
}