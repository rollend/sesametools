
package net.fortytwo.sesametools;

import info.aduna.iteration.CloseableIteration;
import org.openrdf.model.IRI;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.evaluation.TripleSource;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

/**
 * A <code>TripleSource</code> which is based on a <code>SailConnection</code>
 *
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class SailConnectionTripleSource implements TripleSource {
    private SailConnection baseConnection;
    private ValueFactory valueFactory;
    private boolean includeInferred;

    public SailConnectionTripleSource(final SailConnection conn,
                                      final ValueFactory valueFactory,
                                      final boolean includeInferred) {
        baseConnection = conn;
        this.valueFactory = valueFactory;
        this.includeInferred = includeInferred;
    }

    public CloseableIteration<? extends Statement, QueryEvaluationException> getStatements(final Resource subj,
                                                                                           final IRI pred,
                                                                                           final Value obj,
                                                                                           final Resource... contexts) {
        try {
            return new QueryEvaluationIteration(
                    baseConnection.getStatements(subj, pred, obj, includeInferred, contexts));
        } catch (SailException e) {
            return new EmptyCloseableIteration<>();
        }
    }

    public ValueFactory getValueFactory() {
        return valueFactory;
    }

    public static class QueryEvaluationIteration implements CloseableIteration<Statement, QueryEvaluationException> {
        private CloseableIteration<? extends Statement, SailException> baseIteration;

        public QueryEvaluationIteration(final CloseableIteration<? extends Statement, SailException> baseIteration) {
            this.baseIteration = baseIteration;
        }

        public void close() throws QueryEvaluationException {
            try {
                baseIteration.close();
            } catch (SailException e) {
                throw new QueryEvaluationException(e);
            }
        }

        public boolean hasNext() throws QueryEvaluationException {
            try {
                return baseIteration.hasNext();
            } catch (SailException e) {
                throw new QueryEvaluationException(e);
            }
        }

        public Statement next() throws QueryEvaluationException {
            try {
                return baseIteration.next();
            } catch (SailException e) {
                throw new QueryEvaluationException(e);
            }
        }

        public void remove() throws QueryEvaluationException {
            try {
                baseIteration.remove();
            } catch (SailException e) {
                throw new QueryEvaluationException(e);
            }
        }
    }
}
