package cz.mkozeny.dekodar.session;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import cz.mkozeny.dekodar.entity.ApplicationLog.Severity;
import cz.mkozeny.dekodar.entity.Role;
import cz.mkozeny.dekodar.entity.User;
import cz.mkozeny.dekodar.session.applicationlog.ApplicationLogAction;

@Name("authenticator")
public class Authenticator
{
	@Logger private Log log;

    @In EntityManager entityManager;
    
    @In Identity identity;
    @In Credentials credentials;
    
    @Out(scope=ScopeType.SESSION, required=false)
    @In (required = false)
    User signedUser;

    @In ApplicationLogAction applicationLogAction;
    
    public boolean authenticate()
    {
    	try {
    		User user = (User) entityManager.createQuery(
    		"from User where username = :username and password = :password")
    		.setParameter("username", credentials.getUsername())
    		.setParameter("password", PasswordHasher.hashPassword(credentials.getPassword(),credentials.getUsername()))
    		.getSingleResult();
    		if (user.getRoles() != null) {
    			for (Role mr : user.getRoles())
    				identity.addRole(mr.getName());
    		}
    		signedUser = user;
    		user.setLastAccess(new Date());
    		entityManager.merge(user);
    		applicationLogAction.createLog(Severity.INFO, "Přihlášení uživatele "+user.getUsername());
    		return true;
    	}
    	catch (NoResultException ex) {
    		applicationLogAction.createLog(Severity.WARNING, "Neúspěšné přihlášení uživatele "+credentials.getUsername());
    		return false;
    	}
    }
    
    //metoda slouzi k udrzovani session a conversation v ramci keepalive
    public void keepAlive()
	{
		log.info("keepalive: ", signedUser==null?"anonymous":signedUser.getUsername());
	}
}
