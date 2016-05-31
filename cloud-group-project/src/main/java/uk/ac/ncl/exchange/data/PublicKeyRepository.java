package uk.ac.ncl.exchange.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import uk.ac.ncl.exchange.manager.DynamoDBManager;
import uk.ac.ncl.exchange.model.Exchange;
import uk.ac.ncl.exchange.model.PublicKeys;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by B4047409 on 12/03/2015.
 */
public class PublicKeyRepository {

    @Inject
    private
    @Named("logger")
    Logger log;

    @Inject
    private DynamoDBManager manager;

    @Inject
    private ExchangeRepository exchangeRepository;

    /**
     *
     * @param userName
     * @return
     */
    public List<PublicKeys> findAllByUserName(String userName) {
        DynamoDBMapper mapper = manager.getDynamoDBMapper();
        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        expression.setLimit(1000); // is that necessary?
        return mapper.scan(PublicKeys.class, expression);
    }

    /**
     *
     * @param userName
     * @param keyId
     * @return
     */
    public PublicKeys findByUserNameAndKeyId(String userName, String keyId) {
        DynamoDBMapper mapper = manager.getDynamoDBMapper();
        return mapper.load(PublicKeys.class, userName, keyId);
    }

    /**
     *
     * @param publicKey
     */
    public void createPublicKey(PublicKeys publicKey) {
        DynamoDBMapper mapper = manager.getDynamoDBMapper();
        mapper.save(publicKey);
    }
}
