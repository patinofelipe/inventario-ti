
package br.com.inventarioti.repository;

import br.com.inventarioti.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface EquipamentoRepository
        extends JpaRepository<Equipamento, Long> {

    Optional<Equipamento> findByNumero(String numero);
    Optional<Equipamento> findByNrpIgnoreCase(String nrp);

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

    @Query("""
        SELECT COUNT(e)
        FROM Equipamento e
        WHERE e.ip IS NULL OR TRIM(e.ip) = ''
        """)
    long contarSemIp();

    @Query("""
        SELECT COUNT(e)
        FROM Equipamento e
        WHERE e.macAddress IS NULL OR TRIM(e.macAddress) = ''
        """)
    long contarSemMacAddress();

}
