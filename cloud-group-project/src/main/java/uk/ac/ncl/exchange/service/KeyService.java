package uk.ac.ncl.exchange.service;

import uk.ac.ncl.exchange.data.PublicKeyRepository;
import uk.ac.ncl.exchange.data.SessionRepository;
import uk.ac.ncl.exchange.model.PublicKeys;
import uk.ac.ncl.exchange.model.Session;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by B4046044 on 12/03/2015.
 */
public class KeyService {

    @Inject
    PublicKeyRepository publicKeyRepository;

    @Inject
    private SessionRepository sessionRepository;

    @Inject
    @Named("logger")
    private Logger logger;

    public List<PublicKeys> findAllByUserName(String userName) {
        return publicKeyRepository.findAllByUserName(userName);
    }

    public PublicKeys findByUserNameAndKeyId(String userName, String keyId) {
        return publicKeyRepository.findByUserNameAndKeyId(userName, keyId);
    }

    public String createPublicKey(PublicKeys publicKey, String token) {
        Session session = sessionRepository.findByToken(token);
        publicKey.setId(UUID.randomUUID().toString());
        publicKey.setUserName(session.getUserName());
        publicKeyRepository.createPublicKey(publicKey);
        return publicKey.getId();
    }
}
