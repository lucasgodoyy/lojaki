package lojaki.lojavirtual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ServiceContagemAcessoAPI {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void atualizaAcessoEndpointPF() {
        jdbcTemplate.execute("begin; update tabela_acesso_endpoint set qtd_acesso_endpoint = qtd_acesso_endpoint + 1 where nome_end_point = 'endpoint_nome_pessoa'; commit");
    }
}
