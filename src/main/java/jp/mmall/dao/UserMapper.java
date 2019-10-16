package jp.mmall.dao;

import jp.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    //检查用户名是否存在
    int checkUsername(String username);
    //验证密码是否正确
    User selectLogin(@Param("username") String username,@Param("password") String password);

    int checkEmail(String email);
    //密码
    String selectuserByQuestionFindAnswer(String username);
    //检测问题与答案是否一致
    int checkAnswer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);
    //更新密码
    int updatePasswordByUsername(@Param("username") String username,@Param("passwordNew") String passwordNew);
    //通过密码来查询是否对应的是此user
    int checkPassword(@Param("password") String password,@Param("userId") Long userId);
    //查询邮箱是否被使用过
    int checkEmailByUserId(@Param("email")String email,@Param("userId")Long userId);
}