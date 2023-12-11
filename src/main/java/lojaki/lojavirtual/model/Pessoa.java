package lojaki.lojavirtual.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SequenceGenerator(name = "seq_pessoa", sequenceName = "seq_pessoa", allocationSize = 1, initialValue = 1)
public abstract class Pessoa implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_pessoa")
	private Long id;
	
	
	@Size(min = 4, message = "O nome deve ter no mínimo 4 caracteres!")
	@NotBlank(message = "O campo nome deve ser informado!")
	@NotNull(message = "O campo nome deve ser informado!")
	@Column(nullable = false)
	private String nome;
	
	@Email
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String telefone;
	
	@Column
	private String tipoPessoa;

	
	@OneToMany(mappedBy = "pessoa", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Endereco> enderecos = new ArrayList<>();

	
	@ManyToOne(targetEntity = Pessoa.class)
	@JoinColumn(name = "empresa_id", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "empresa_id_fk"))
	private Pessoa empresa;
	
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getTelefone() {
		return telefone;
	}


	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	
	public void setEnderecos(List<Endereco> enderecos) {
		this.enderecos = enderecos;
	}
	
	public List<Endereco> getEnderecos() {
		return enderecos;
	}

	
	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}
	
	public String getTipoPessoa() {
		return tipoPessoa;
	}

	
	
	
	
	public Pessoa getEmpresa() {
		return empresa;
	}


	public void setEmpresa(Pessoa empresa) {
		this.empresa = empresa;
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
		Pessoa other = (Pessoa) obj;
		return Objects.equals(id, other.id);
	}
	
	
	
	
}
