package com.example.demo.repository;

import com.example.demo.entity.Exercicio;
import com.example.demo.entity.ItemTreino;
import com.example.demo.entity.PlanoTreino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com a entidade ItemTreino
 */
@Repository
public interface ItemTreinoRepository extends JpaRepository<ItemTreino, Long> {
    
    /**
     * Busca todos os itens de um plano de treino
     * @param planoTreino Plano de treino
     * @return Lista de itens
     */
    List<ItemTreino> findByPlanoTreino(PlanoTreino planoTreino);
    
    /**
     * Busca todos os planos que incluem um exercício específico
     * @param exercicio Exercício
     * @return Lista de itens
     */
    List<ItemTreino> findByExercicio(Exercicio exercicio);
    
    /**
     * Busca item específico de um plano com um exercício
     * @param planoTreino Plano de treino
     * @param exercicio Exercício
     * @return Optional com o item se encontrado
     */
    Optional<ItemTreino> findByPlanoTreinoAndExercicio(PlanoTreino planoTreino, Exercicio exercicio);
    
    /**
     * Busca itens por número de séries
     * @param series Número de séries
     * @return Lista de itens
     */
    List<ItemTreino> findBySeries(Integer series);
    
    /**
     * Verifica se já existe um exercício no plano
     * @param planoTreino Plano de treino
     * @param exercicio Exercício
     * @return true se já existe, false caso contrário
     */
    boolean existsByPlanoTreinoAndExercicio(PlanoTreino planoTreino, Exercicio exercicio);
    
    /**
     * Conta quantos exercícios existem em um plano
     * @param planoTreino Plano de treino
     * @return Número de exercícios
     */
    long countByPlanoTreino(PlanoTreino planoTreino);
    
    /**
     * Busca itens por número de repetições
     * @param repeticoes Número de repetições
     * @return Lista de itens
     */
    List<ItemTreino> findByRepeticoes(Integer repeticoes);
    
    /**
     * Busca itens por carga específica
     * @param carga Carga utilizada
     * @return Lista de itens
     */
    List<ItemTreino> findByCarga(BigDecimal carga);
    
    /**
     * Busca itens com carga maior ou igual a um valor
     * @param carga Carga mínima
     * @return Lista de itens
     */
    List<ItemTreino> findByCargaGreaterThanEqual(BigDecimal carga);
    
    /**
     * Busca itens com carga menor ou igual a um valor
     * @param carga Carga máxima
     * @return Lista de itens
     */
    List<ItemTreino> findByCargaLessThanEqual(BigDecimal carga);
    
    /**
     * Busca itens de um plano ordenados por séries
     * @param planoTreino Plano de treino
     * @return Lista de itens ordenados
     */
    List<ItemTreino> findByPlanoTreinoOrderBySeriesAsc(PlanoTreino planoTreino);
    
    /**
     * Busca itens de um plano ordenados por carga
     * @param planoTreino Plano de treino
     * @return Lista de itens ordenados por carga
     */
    List<ItemTreino> findByPlanoTreinoOrderByCargaDesc(PlanoTreino planoTreino);
    
    /**
     * Conta quantos itens (exercícios) tem um plano de treino
     * @param planoTreino Plano de treino
     * @return Número de exercícios no plano
     */
    long countByPlanoTreino(PlanoTreino planoTreino);
    
    /**
     * Conta em quantos planos um exercício aparece
     * @param exercicio Exercício
     * @return Número de planos que incluem o exercício
     */
    long countByExercicio(Exercicio exercicio);
    
    /**
     * Verifica se um plano contém um exercício específico
     * @param planoTreino Plano de treino
     * @param exercicio Exercício
     * @return true se o plano contém o exercício
     */
    boolean existsByPlanoTreinoAndExercicio(PlanoTreino planoTreino, Exercicio exercicio);
    
    /**
     * Busca itens de um plano que usam uma carga específica
     * @param planoTreino Plano de treino
     * @param carga Carga
     * @return Lista de itens
     */
    List<ItemTreino> findByPlanoTreinoAndCarga(PlanoTreino planoTreino, BigDecimal carga);
    
    /**
     * Busca todos os itens ordenados por plano e séries
     * @return Lista de itens ordenados
     */
    List<ItemTreino> findAllByOrderByPlanoTreinoAscSeriesAsc();
    
    /**
     * Busca itens com observações não nulas
     * @return Lista de itens com observações
     */
    List<ItemTreino> findByObservacoesIsNotNull();
    
    /**
     * Busca itens de um plano com séries e repetições específicas
     * @param planoTreino Plano de treino
     * @param series Número de séries
     * @param repeticoes Número de repetições
     * @return Lista de itens
     */
    List<ItemTreino> findByPlanoTreinoAndSeriesAndRepeticoes(PlanoTreino planoTreino, Integer series, Integer repeticoes);
    
    /**
     * Busca a carga máxima utilizada em um plano
     * @param planoTreino Plano de treino
     * @return Carga máxima
     */
    @Query("SELECT MAX(i.carga) FROM ItemTreino i WHERE i.planoTreino = :planoTreino")
    BigDecimal buscarCargaMaximaDoPlano(PlanoTreino planoTreino);
    
    /**
     * Busca todos os exercícios únicos de um plano
     * @param planoTreino Plano de treino
     * @return Lista de exercícios
     */
    @Query("SELECT DISTINCT i.exercicio FROM ItemTreino i WHERE i.planoTreino = :planoTreino")
    List<Exercicio> buscarExerciciosDoPlano(PlanoTreino planoTreino);
}
