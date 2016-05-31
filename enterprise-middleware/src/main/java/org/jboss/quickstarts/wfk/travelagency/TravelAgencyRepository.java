package org.jboss.quickstarts.wfk.travelagency;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class TravelAgencyRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;

    TravelAgencyBooking findById(Long id) throws Exception{
        return em.find(TravelAgencyBooking.class, id);
    }
    List<TravelAgencyBooking> findAll() {
        TypedQuery<TravelAgencyBooking> query = em.createNamedQuery(TravelAgencyBooking.FIND_ALL, TravelAgencyBooking.class);
        return query.getResultList();
    }
    
    TravelAgencyBooking delete(TravelAgencyBooking booking) throws Exception {
        log.info("TravelAgencyRepository.delete() - Deleting " + booking.toString());
        if (booking.getId() != null) {
            em.remove(em.merge(booking));
        } else {
            log.info("TravelAgencyRepository.delete() - No ID was found so can't Delete.");
        }
        return booking;
    }
    
    
    TravelAgencyBooking create(TravelAgencyBooking booking) throws Exception {
        log.info("TravelAgencyRepository.create() - Creating " + booking.toString());
        
        em.persist(booking);
        
        return booking;
    }
    
}
