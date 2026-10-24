
package br.com.inventarioti.repository;

import br.com.inventarioti.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipamentoRepository
        extends JpaRepository<Equipamento, Long> {

    Optional<Equipamento> findByNumero(String numero);

    List<Equipamento> findByLocalidadeContainingIgnoreCase(
            String localidade);

    List<Equipamento> findByNrpContainingIgnoreCase(String nrp);

    List<Equipamento> findByIpStartingWith(String prefixo);
}
