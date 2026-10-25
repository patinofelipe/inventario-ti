
package br.com.inventarioti.repository;

import br.com.inventarioti.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface EquipamentoRepository
        extends JpaRepository<Equipamento, Long> {

    Optional<Equipamento> findByNumero(String numero);

    List<Equipamento> findByLocalidadeContainingIgnoreCase(
            String localidade);

    List<Equipamento> findByNrpContainingIgnoreCase(String nrp);

    List<Equipamento> findByIpStartingWith(String prefixo);

    long countByStatusIgnoreCase(String status);

    @Query("""
    SELECT e.localidade, COUNT(e)
    FROM Equipamento e
    GROUP BY e.localidade
    ORDER BY e.localidade
    """)
List<Object[]> contarPorLocalidade();
}
