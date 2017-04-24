package edu.uci.ics.textdb.exp.sink;

import edu.uci.ics.textdb.api.constants.ErrorMessages;
import edu.uci.ics.textdb.api.dataflow.IOperator;
import edu.uci.ics.textdb.api.dataflow.ISink;
import edu.uci.ics.textdb.api.exception.DataFlowException;
import edu.uci.ics.textdb.api.exception.TextDBException;
import edu.uci.ics.textdb.api.schema.Schema;
import edu.uci.ics.textdb.api.tuple.Tuple;

/**
 * Created by chenli on 5/11/16.
 *
 * This abstract class leaves the @processOneTuple() function to be implemented
 * by the subclass based on the logic of handling each tuple coming from the
 * subtree.
 *
 */
public abstract class AbstractSink implements ISink {

    private IOperator inputOperator;
    private int cursor = CLOSED;

    /**
     * @about Opens the child operator.
     */
    @Override
    public void open() throws TextDBException {
        if (cursor != CLOSED) {
            return;
        }
        inputOperator.open();
        cursor = OPENED;
    }

    public void setInputOperator(IOperator inputOperator) {
        this.inputOperator = inputOperator;
    }

    public IOperator getInputOperator() {
        return this.inputOperator;
    }

    @Override
    public void processTuples() throws TextDBException {
        if (cursor == CLOSED) {
            throw new DataFlowException(ErrorMessages.OPERATOR_NOT_OPENED);
        }
        Tuple nextTuple;

        while ((nextTuple = inputOperator.getNextTuple()) != null) {
            processOneTuple(nextTuple);
            cursor++;
        }
    }

    /**
     *
     * @param nextTuple
     *            A tuple that needs to be processed during each iteration
     */
    protected abstract void processOneTuple(Tuple nextTuple) throws TextDBException;

    @Override
    public void close() throws TextDBException {
        if (cursor == CLOSED) {
            return;
        }
        inputOperator.close();
        cursor = CLOSED;
    }
    
    public Schema getOutputSchema() {
        return this.inputOperator.getOutputSchema();
    }
}
