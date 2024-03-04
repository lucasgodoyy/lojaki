package lojaki.lojavirtual.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lojaki.lojavirtual.enums.StatusVendaLojaVirtual;

@Entity
@Table(name = "vd_cp_loja_virt")
@SequenceGenerator(name = "seq_vd_cp_loja_virt", sequenceName = "seq_vd_cp_loja_virt", allocationSize = 1, initialValue = 1)
public class VendaCompraLojaVirtual implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_vd_cp_loja_virt")
	private Long id;

	@NotNull(message = "A pessoa compradora deve ser informada")
	@ManyToOne(targetEntity = PessoaFisica.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "pessoa_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "pessoa_fk"))
	private PessoaFisica pessoa;

	@NotNull(message = "O endereço de entrega deve ser informado")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "endereco_entrega_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "endereco_entrega_fk"))
	private Endereco enderecoEntrega;

	@NotNull(message = "O endereço de cobrança deve ser informado")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "endereco_cobranca_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "endereco_cobranca_fk"))
	private Endereco enderecoCobranca;

	@Min(value = 1, message = "Valor total da venda é inválido")
	@Column(nullable = false)
	private BigDecimal valorTotal;

	private BigDecimal valorDesconto;

	
	@NotNull(message = "A forma de pagamento deve ser informado")
	@ManyToOne
	@JoinColumn(name = "forma_pagamento_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "forma_pagamento_fk"))
	private FormaPagamento formaPagamento;

	@JsonIgnoreProperties(allowGetters = true)
	@NotNull(message = "A nota fiscal deve ser informada")
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "nota_fiscal_venda_id", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "nota_fiscal_venda_fk"))
	private NotaFiscalVenda notaFiscalVenda;

	@ManyToOne
	@JoinColumn(name = "cupom_desc_id", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "cupom_desc_fk"))
	private CupomDesconto cupomDesconto;

	
	@Min(value = 5, message = "valor do frete é inválido")
	@NotNull(message = "O valor do frete deve ser informado")
	@Column(nullable = false)
	private BigDecimal valorFrete;

	@NotNull(message = "Data da entrega deve ser informada")
	@Min(value = 1, message = "Dia de entrega é inválido")
	@Column(nullable = false)
	private Integer diasEntrega;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataVenda;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataEntrega;

	
	@NotNull(message = "A empresa dona do registro deve ser informada")
	@ManyToOne(targetEntity = PessoaJuridica.class)
	@JoinColumn(name = "empresa_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "empresa_id_fk"))
	private PessoaJuridica empresa;
	
	
	@NotNull(message = "Status da venda ou compra deve ser informado")
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private StatusVendaLojaVirtual statusVendaLojaVirtual;
	
	
	@OneToMany(mappedBy = "vendaCompraLojaVirtual", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<ItemVendaLoja> itemVendaLojas = new ArrayList<ItemVendaLoja>();

	
	
	
	private Boolean excluido = Boolean.FALSE;
	
	
	@Column(name = "codigo_etiqueta")
	private String codigoEtiqueta;

	@Column(name = "url_imprime_etiqueta")
	private String urlImprimeEtiqueta;
	
	
	public void setExcluido(Boolean excluido) {
		this.excluido = excluido;
	}

	public Boolean getExcluido() {
		return excluido;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PessoaFisica getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaFisica pessoa) {
		this.pessoa = pessoa;
	}

	public Endereco getEnderecoEntrega() {
		return enderecoEntrega;
	}

	public void setEnderecoEntrega(Endereco enderecoEntrega) {
		this.enderecoEntrega = enderecoEntrega;
	}

	public Endereco getEnderecoCobranca() {
		return enderecoCobranca;
	}

	public void setEnderecoCobranca(Endereco enderecoCobranca) {
		this.enderecoCobranca = enderecoCobranca;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public BigDecimal getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(BigDecimal valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public NotaFiscalVenda getNotaFiscalVenda() {
		return notaFiscalVenda;
	}

	public void setNotaFiscalVenda(NotaFiscalVenda notaFiscalVenda) {
		this.notaFiscalVenda = notaFiscalVenda;
	}

	public CupomDesconto getCupomDesconto() {
		return cupomDesconto;
	}

	public void setCupomDesconto(CupomDesconto cupomDesconto) {
		this.cupomDesconto = cupomDesconto;
	}

	public BigDecimal getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}

	public Integer getDiasEntrega() {
		return diasEntrega;
	}

	public void setDiasEntrega(Integer diasEntrega) {
		this.diasEntrega = diasEntrega;
	}

	public Date getDataVenda() {
		return dataVenda;
	}

	public void setDataVenda(Date dataVenda) {
		this.dataVenda = dataVenda;
	}

	public Date getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
	}
	
	public void setEmpresa(PessoaJuridica empresa) {
		this.empresa = empresa;
	}
	
	public PessoaJuridica getEmpresa() {
		return empresa;
	}

	public void setItemVendaLojas(List<ItemVendaLoja> itemVendaLojas) {
		this.itemVendaLojas = itemVendaLojas;
	}
	
	public List<ItemVendaLoja> getItemVendaLojas() {
		return itemVendaLojas;
	}
	
	/* Frete que foi escolhido pelo cliente no momento da compra */
	private String servicoTransportadora;
	
	
	public StatusVendaLojaVirtual getStatusVendaLojaVirtual() {
		return statusVendaLojaVirtual;
	}

	public void setStatusVendaLojaVirtual(StatusVendaLojaVirtual statusVendaLojaVirtual) {
		this.statusVendaLojaVirtual = statusVendaLojaVirtual;
	}

	public String getCodigoEtiqueta() {
		return codigoEtiqueta;
	}

	public void setCodigoEtiqueta(String codigoEtiqueta) {
		this.codigoEtiqueta = codigoEtiqueta;
	}

	public String getUrlImprimeEtiqueta() {
		return urlImprimeEtiqueta;
	}

	public void setUrlImprimeEtiqueta(String urlImprimeEtiqueta) {
		this.urlImprimeEtiqueta = urlImprimeEtiqueta;
	}

	public void setServicoTransportadora(String servicoTransportadora) {
		this.servicoTransportadora = servicoTransportadora;
	}
	
	public String getServicoTransportadora() {
		return servicoTransportadora;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VendaCompraLojaVirtual other = (VendaCompraLojaVirtual) obj;
		return Objects.equals(id, other.id);
	}

}
