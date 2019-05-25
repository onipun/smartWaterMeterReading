package com.example.psm.v1.model;

/**
 * PersonRepository
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import com.example.psm.v1.database.Person;

import java.util.List;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class PersonRepository {

    public static final String BASE_DN = "dc=springframework,dc=org";

    @Autowired
    private LdapTemplate ldapTemplate;

    /**
     * Retrieves all the persons in the ldap server
     * @return list of person names
     */
    public List<String> getAllPersonNames() {
        return ldapTemplate.search(
                query().where("objectclass").is("person"),
                new AttributesMapper<String>() {
                    public String mapFromAttributes(Attributes attrs)
                            throws NamingException {
                        return (String) attrs.get("cn").get();
                    }
                });
    }

    public boolean create(Person p) {
        Name dn = buildDn(p);
        ldapTemplate.bind(dn, null, buildAttributes(p));
        
        return true;
     }

     private Name buildDn(Person p) {
        return LdapNameBuilder.newInstance(BASE_DN)
                .add("ou", "people")
                .add("uid", p.getName())
                .build();
    }

    private Attributes buildAttributes(Person p) {
        Attributes attrs = new BasicAttributes();
        BasicAttribute ocAttr = new BasicAttribute("objectclass");
        ocAttr.add("top");
        ocAttr.add("person");
        ocAttr.add("organizationalPerson");
        ocAttr.add("inetOrgPerson");
        attrs.put(ocAttr);
        attrs.put("cn", p.getName());
        attrs.put("sn", p.getName());
        attrs.put("uid", p.getName());
        attrs.put("userPassword", p.getName()+"spassword");
        return attrs;
    }

    private String digestSHA(String pass){
        LdapShaPasswordEncoder ldapShaPasswordEncoder=  new LdapShaPasswordEncoder();
        return ldapShaPasswordEncoder.encode(pass);
    }

}