package com.example.projectiii.specification;

import com.example.projectiii.constant.RoleType;
import com.example.projectiii.entity.Role;
import com.example.projectiii.entity.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> likeUserName(String name) {
        return (root, query, cb)
                -> cb.like(root.get("userName"), "%" + name.toLowerCase() + "%");
    }
    public static Specification<User> hasUserID(Integer userID){
        return (root, query, cb)
                -> cb.equal(root.get("userID"), userID);
    }
    public static Specification<User> likeFullName(String userName){
        return (root, query, cb)
                -> cb.like(root.get("userName"), "%" + userName.toLowerCase() + "%");
    }
    public static Specification<User> likePhoneNumber(String phoneNumber){
        return((root, query, cb) ->
                cb.equal(root.get("phoneNumber"), "%" + phoneNumber + "%"));
    }
    public static Specification<User> likeAddress(String address){
        return (root, query, cb)
                -> cb.like(root.get("address"), "%" + address + "%");
    }
   /* public static Specification<User> hasRole(String roleName){
        return (root, query, cb) -> {
            Join<User, Role> roleJoin = root.join("role");
            return cb.equal(roleJoin.get("name"), roleName.toLowerCase());
        };
    }*/
    public static Specification<User> likeGmail(String gmail){
        return (root, query, cb)
                -> cb.like(root.get("gmail"), "%" + gmail.toLowerCase() + "%");
    }
    public static Specification<User> hasRole(RoleType roleType){
        return (root, query, cb) -> {
            Join<User, Role> roleJoin = root.join("role");
            return cb.equal(roleJoin.get("roleName"), roleType);
        };
    }



}
