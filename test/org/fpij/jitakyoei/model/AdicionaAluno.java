package org.fpij.jitakyoei.model;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import core.entities.Aluno;
import core.entities.Endereco;
import core.entities.Entidade;
import core.entities.Filiado;
import core.entities.Professor;
import core.entities.Rg;
import core.validators.AlunoValidator;
import infra.interfaces.IDAO;
import infra.dao.DAO;
import org.junit.BeforeClass;
import org.junit.Test;

import app.utils.CPFValidator;
import app.utils.DatabaseManager;

public class AddAlunoTest {
	
	private static IDAO<Aluno> alunoDao;
	private static Aluno aluno;
	private static Entidade entidade;
	private static Endereco endereco;
	private static Filiado filiado;
	private static Filiado filiadoProf;
	private static Professor professor;
	private static Rg rg;
	
	@BeforeClass
	public static void setUp(){
		DatabaseManager.setEnviroment(DatabaseManager.TEST);

		rg = new Rg();
		rg.setNumero("444.333.222-1");
		rg.setOrgaoExpedidor("SSP");
		
		endereco = new Endereco();
		endereco.setRua("Av humberto alencar castelo branco");
		endereco.setCep("09850-900");
		endereco.setCidade("São Bernardo do Campo");
		endereco.setEstado("SP");
		endereco.setNumero("4110");
		endereco.setBairro("Assunção");
		
		filiadoProf = new Filiado();
		filiadoProf.setNome("Kleber Namoto");
		filiadoProf.setCpf("036.464.453-27");
		filiadoProf.setDataNascimento(new Date());
		filiadoProf.setDataCadastro(new Date());
		filiadoProf.setId(1123);
		filiadoProf.setEndereco(endereco);
		
		professor = new Professor();
		professor.setFiliado(filiadoProf);
		
		entidade = new Entidade();
		entidade.setEndereco(endereco);
		entidade.setNome("Claudio");
		entidade.setTelefone1("(11)9999-9999");
		
		alunoDao = new DAO<Aluno>(Aluno.class, new AlunoValidator(), true);
	}

	public static void clearDatabase(){
		List<Aluno> all = alunoDao.list();
		for (Aluno each : all) {
			alunoDao.delete(each);
		}
		assertEquals(0, alunoDao.list().size());
	}
	
	// Cenário 01
	@Test
	public void ReturnsOk() throws Exception {
		// Arange
		clearDatabase();

		filiado = new Filiado();
		filiado.setNome("Eduardo Vieira");
		filiado.setCpf("111.222.333-44");
		filiado.setDataNascimento(new Date());
		filiado.setDataCadastro(new Date());
		filiado.setEmail("vierira.edu@outlook.com");
		filiado.setRg(rg);
		filiado.setTelefone1("1144343343");
		filiado.setId(1234);
		filiado.setEndereco(endereco);

		aluno = new Aluno();
		aluno.setFiliado(filiado);
		aluno.setProfessor(professor);
		aluno.setEntidade(entidade);
		
		// Act
		boolean isAlunoSaved = alunoDao.save(aluno);

		// Assert
		assertEquals(true, isAlunoSaved);
		assertEquals(1, alunoDao.list().size());

		assertEquals("111.222.333-44", CPFValidator.imprimeCPF(alunoDao.get(aluno).getFiliado().getCpf()));
		assertEquals("Eduardo Vieira", alunoDao.get(aluno).getFiliado().getNome());
		assertEquals("vierira.edu@outlook.com", alunoDao.get(aluno).getFiliado().getEmail());
		assertEquals("18.889.037-3", alunoDao.get(aluno).getFiliado().getRg().getNumero());
		assertEquals("SSP", alunoDao.get(aluno).getFiliado().getRg().getOrgaoExpedidor());
		assertEquals("444.333.222-1", alunoDao.get(aluno).getFiliado().getTelefone1());
		assertEquals("Kleber Machado", alunoDao.get(aluno).getProfessor().getFiliado().getNome());
		assertEquals("Claudio", alunoDao.get(aluno).getEntidade().getNome());
		assertEquals("09850-900", alunoDao.get(aluno).getProfessor().getFiliado().getEndereco().getCep());
		assertEquals("Assunção", alunoDao.get(aluno).getProfessor().getFiliado().getEndereco().getBairro());
	}
	
	// Cenário 02
	@Test
	public void ReturnsInvalidData() throws Exception {
		// Arange
		clearDatabase();

		filiado = new Filiado();
		filiado.setNome("Vitorio Lotto");
		filiado.setCpf("11122233344");
		filiado.setDataNascimento(new Date());
		filiado.setDataCadastro(new Date());
		filiado.setEmail("vierira.edu@outlook.com");
		filiado.setRg(rg);
		filiado.setTelefone1("44343343");
		filiado.setId(1234);

		aluno = new Aluno();
		aluno.setFiliado(filiado);
		aluno.setProfessor(professor);
		aluno.setEntidade(entidade);
		
		// Act
		boolean isAlunoSaved = alunoDao.save(aluno);

		// Assert
		assertEquals(false, isAlunoSaved);
		assertEquals(0, alunoDao.list().size());

		assertEquals(false, CPFValidator.isCPF(filiado.getCpf()));
	}

	// Cenário 03
	@Test
	public void ReturnsAbsentData() throws Exception {
		// Arange
		clearDatabase();

		filiado = new Filiado();
		filiado.setNome("Eduardo Vieira");
		filiado.setCpf("");
		filiado.setDataNascimento(new Date());
		filiado.setDataCadastro(new Date());
		filiado.setEmail("vierira.edu@outlook.com");
		filiado.setRg(rg);
		filiado.setTelefone1("");
		filiado.setId(1234);

		aluno = new Aluno();
		aluno.setFiliado(filiado);
		aluno.setProfessor(professor);
		aluno.setEntidade(entidade);
		
		// Act
		boolean isAlunoSaved = alunoDao.save(aluno);

		// Assert
		assertEquals(false, isAlunoSaved);
		assertEquals(0, alunoDao.list().size());
	}
	
}
