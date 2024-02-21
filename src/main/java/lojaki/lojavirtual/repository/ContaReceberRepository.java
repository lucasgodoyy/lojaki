package lojaki.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lojaki.lojavirtual.model.ContaReceber;

@Repository
public interface ContaReceberRepository extends JpaRepository<ContaReceber, Long> {

}