package com.learn.SpendingManagement.repository.spendingmanagement;

import com.learn.SpendingManagement.dto.response.User.AccountResponse;
import com.learn.SpendingManagement.dto.response.User.UserResponse;
import com.learn.SpendingManagement.dto.response.spendingmanagement.TagFinanceResponse;
import com.learn.SpendingManagement.entity.spendingmanagement.TagFinance;
import com.learn.SpendingManagement.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TagFinanceRepository extends BaseRepository<TagFinance> {
  @Query("""
          select new com.learn.SpendingManagement.dto.response.spendingmanagement.TagFinanceResponse
          (t.id,t.name,t.description)
          FROM TagFinance t
          inner join User u on t.userId=u.id
          inner join Account a on a.id=u.accountId
          where a.username =:username and t.isDeleted= false
        """)
  Page<TagFinanceResponse> searchAll(PageRequest pageRequest, String username);

  @Query("""
          select new com.learn.SpendingManagement.dto.response.spendingmanagement.TagFinanceResponse
          (t.id,t.name,t.description)
          FROM TagFinance t
          left join User u on t.userId=u.id
          inner join Account a on a.id=u.accountId
          WHERE(:keyword is null or
          (lower(t.name) LIKE lower(concat('%', :keyword, '%'))))
          and a.username =:username and t.isDeleted= false
        """)
  Page<TagFinanceResponse> searchByKey(PageRequest pageRequest, String keyword, String username);


  @Query("""
          select new com.learn.SpendingManagement.dto.response.spendingmanagement.TagFinanceResponse
          (t.id,t.name,t.description)
          FROM TagFinance t
          where t.isDeleted= false
        """)
  Page<TagFinanceResponse> searchAllForAdmin(PageRequest pageRequest);

  @Query("""
          select new com.learn.SpendingManagement.dto.response.spendingmanagement.TagFinanceResponse
          (t.id,t.name,t.description)
          FROM TagFinance t
          WHERE(:keyword is null or
          (lower(t.name) LIKE lower(concat('%', :keyword, '%'))))
           AND t.isDeleted = false
        """)
  Page<TagFinanceResponse> searchByKeyForAdmin(PageRequest pageRequest, String keyword);

  @Query("""
         SELECT CASE WHEN COUNT(a) > 0
         THEN true ELSE false END FROM TagFinance a
         WHERE a.name = :name AND a.isDeleted= false
        """)
  boolean existByName(String name);

  @Transactional
  @Modifying
  @Query("""
        update TagFinance t set t.isDeleted=true where t.id=:id
        """)
  void delete(String id);

  @Query("""
        SELECT new com.learn.SpendingManagement.dto.response.User.UserResponse
        (u.id, a.id, a.username)
        FROM User u
        LEFT JOIN Account a ON a.id = u.accountId
        WHERE u.isDeleted=false and u.id= :id
        """)
  UserResponse findAllUsername(String id);
}
