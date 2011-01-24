package de.osmembrane.model.pipeline;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;

import de.osmembrane.model.Identifier;
import de.osmembrane.model.ModelProxy;
import de.osmembrane.model.xml.XMLPipe;
import de.osmembrane.tools.I18N;

/**
 * Implements {@link AbstractConnector}
 * 
 * @author jakob_jarosch
 */
public class Connector extends AbstractConnector {

	private static final long serialVersionUID = 2011010722340001L;

	private List<AbstractConnector> connectors = new ArrayList<AbstractConnector>();
	private ConnectorType type;
	private ConnectorPosition position;

	private AbstractFunction parent;
	
	transient private XMLPipe xmlPipe;
	private Identifier xmlPipeIdentifier;

	/**
	 * Constructor for a new Connector with given {@link XMLPipe} and
	 * {@link AbstractFunction} as parent.
	 * 
	 * @param pipe
	 * @param parent
	 */
	public Connector(AbstractFunction parent, ConnectorPosition position,
			XMLPipe xmlPipe) {
		this.parent = parent;
		this.position = position;
		this.type = ConnectorType.parseString(xmlPipe.getType());
		this.xmlPipe = xmlPipe;

		/* set the identifier */
		AbstractFunctionPrototype afp = ModelProxy.getInstance().accessFunctions();
		this.xmlPipeIdentifier = afp.getMatchingXMLPipeIdentifier(this.xmlPipe);
	}

	@Override
	public AbstractFunction getParent() {
		return parent;
	}

	@Override
	public String getDescription() {
		return I18N.getInstance().getDescription(xmlPipe);
	}

	@Override
	public ConnectorType getType() {
		return type;
	}

	@Override
	public int getMaxConnections() {
		if (position == ConnectorPosition.IN) {
			return getType().getMaxInConnections();
		} else {
			return getType().getMaxOutConnections();
		}
	}

	@Override
	public boolean isFull() {
		return (connectors.size() >= getMaxConnections());
	}

	@Override
	public Connector[] getConnections() {
		Connector[] connectors = new Connector[this.connectors.size()];
		connectors = this.connectors.toArray(connectors);
		return connectors;
	}

	@Override
	protected boolean addConnection(AbstractConnector connector) {
		/*
		 * check if the connector is not full and both connector-types does
		 * equal.
		 */
		if (!isFull() && getType() == connector.getType()) {
			connectors.add(connector);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean removeConnection(AbstractConnector connector) {
		return connectors.remove(connector);
	}

	@Override
	protected void unlink(boolean isOutConnector) {
		for (AbstractConnector connector : getConnections()) {
			connector.removeConnection(this);
			this.removeConnection(connector);
		}
	}

	@Override
	public Connector copy(CopyType type) {
		return copy(type, null);
	}

	@Override
	public Connector copy(CopyType type, AbstractFunction parent) {
		Connector newConnector = new Connector(this.parent, this.position, this.xmlPipe);
		
		if (parent != null) {
			newConnector.parent = parent;
		}
		
		return newConnector;
	}
	
	private Object readResolve() throws ObjectStreamException {
		AbstractFunctionPrototype afp = ModelProxy.getInstance().accessFunctions();
		this.xmlPipe = afp.getMatchingXMLPipe(this.xmlPipeIdentifier);
		
		return this;
	}
}