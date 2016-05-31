package uk.ac.ncl.exchange.util;

/**
 * Created by Donatas Daubaras on 07/03/2015.
 *
 * Last Updated By: Sheryl Coe
 * Last Updated Date: 09/03/2015
 *
 * Details of the Status of a document exchange, as follows:
 *
 * NB: Sender is the user who is starting the exchange and sending a document, Receiver is the user who is receiving the document.
 *
 * IN_PROGRESS - the sender has sent the document and the NRO (evidence of origin) to TTP service
 * SUCCESS - the receiver has received the NRO and has sent the NRR (evidence of receipt) to the TTP service, the NRR and document will now be available to sender/receiver
 * ABORT - the document exchange has been aborted by the sender so no further actions can be taken
 * REJECT - the receiver has declined the document exchange
 *
 */
public enum Status {
    IN_PROGRESS,
    SUCCESS,
    ABORT,
    REJECT
}
