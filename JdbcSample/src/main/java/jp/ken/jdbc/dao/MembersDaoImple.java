package jp.ken.jdbc.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import jp.ken.jdbc.entity.Members;
import jp.ken.jdbc.model.MembersSearchModel;

@Component
public class MembersDaoImple implements MembersDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private PlatformTransactionManager transactionManager;

	private RowMapper<Members> membersMapper = new BeanPropertyRowMapper<Members>(Members.class);

	public List<Members> getList(){
		String sql = "SELECT * FROM members";
		List<Members> memberList = jdbcTemplate.query(sql,membersMapper);
		return memberList;
	}

	public List<Members> getListByName(String name){
		String sql = "SELECT * FROM members WHERE name LIKE ?";
		name = name.replace("%","\\%").replace("_", "\\_");
		name = "%" + name + "%";
		Object[] parameters = { name };
		List<Members> membersList = jdbcTemplate.query(sql,parameters,membersMapper);
		return membersList;
	}

	public Members getMembersById(Integer id) {
		String sql = "SELECT * FROM members WHERE id=?";
		Object[] parameters = { id };
		try {
			Members members = jdbcTemplate.queryForObject(sql,parameters, membersMapper);
			return members;
		}catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int insert(Members members) {
		String sql = "INSERT INTO members(name,email,phoneNumber,birthday) VALUES(?,?,?,?);";
		Object[] parameters = { members.getName(), members.getEmail(), members.getPhoneNumber(),members.getBirthday()};
		TransactionStatus transactionStatus = null;
		DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
		int numberRow = 0;
		try {
			transactionStatus = transactionManager.getTransaction(transactionDefinition);
			numberRow = jdbcTemplate.update(sql, parameters);
			transactionManager.commit(transactionStatus);
		}catch ( DataAccessException e) {
			e.printStackTrace();
			transactionManager.rollback(transactionStatus);
		}catch(TransactionException e) {
			e.printStackTrace();
			if ( transactionStatus != null) {
				transactionManager.rollback(transactionStatus);
			}
		}
		return numberRow;
	}

	public int delete(MembersSearchModel searchModel) {
		System.out.println("削除メソッド実行");
		String sql = "DELETE FROM members WHERE id=?";
		Object parameter = searchModel.getId();
		TransactionStatus transactionStatus = null;
		DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
		int numberRow = 0;
		try {
			transactionStatus = transactionManager.getTransaction(transactionDefinition);
			numberRow = jdbcTemplate.update(sql, parameter);
			transactionManager.commit(transactionStatus);
			System.out.println(numberRow);
		}catch ( DataAccessException e) {
			e.printStackTrace();
			transactionManager.rollback(transactionStatus);
		}catch(TransactionException e) {
			e.printStackTrace();
			if ( transactionStatus != null) {
				transactionManager.rollback(transactionStatus);
			}
		}
		return numberRow;

	}

	public int update(Members members) {
		String sql = "UPDATE members SET name=?, email=?, phoneNumber=?, birthday=? WHERE id=?;";
		Object[] parameters = {members.getName(),members.getEmail(),members.getPhoneNumber(),members.getBirthday(),members.getId()};
		System.out.println(members.getName()+":"+members.getBirthday()+"ID:"+members.getId());

		TransactionStatus transactionStatus = null;
		DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
		int numberRow = 0;
		try {
			transactionStatus = transactionManager.getTransaction(transactionDefinition);
			numberRow = jdbcTemplate.update(sql, parameters);
			transactionManager.commit(transactionStatus);
		}catch ( DataAccessException e) {
			e.printStackTrace();
			transactionManager.rollback(transactionStatus);
			System.out.println("DataAccessException");
		}catch(TransactionException e) {
			e.printStackTrace();
			if ( transactionStatus != null) {
				transactionManager.rollback(transactionStatus);
				System.out.println("TransactionException");
			}
		}
		System.out.println("SQL実行結果:"+numberRow);
		return numberRow;
	}


}
