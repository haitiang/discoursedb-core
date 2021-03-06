package edu.cmu.cs.lti.discoursedb.core.service.macro;

import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.QDiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;

public final class DiscoursePartPredicates {

	private DiscoursePartPredicates() {
	}

	public static BooleanExpression discoursePartHasDiscourse(Discourse discourse) {
		if (discourse == null) {
			return QDiscoursePart.discoursePart.isNull();
		} else {
			return QDiscoursePart.discoursePart.discourseToDiscourseParts.any().discourse.eq(discourse);
		}
	}

	public static BooleanExpression discoursePartHasName(String name) {
		if (name == null || name.isEmpty()) {
			return QDiscoursePart.discoursePart.isNull();
		} else {
			return QDiscoursePart.discoursePart.name.eq(name);
		}
	}

	public static BooleanExpression discoursePartHasType(DiscoursePartTypes type) {
		if (type == null) {
			return QDiscoursePart.discoursePart.isNull();
		} else {
			return QDiscoursePart.discoursePart.type.eq(type.name());
		}
	}
	
	public static BooleanExpression discoursePartHasDataSource(DataSourceInstance dataSource) {
		if (dataSource == null) {
			return QDiscoursePart.discoursePart.isNull();
		} else {
			return QDiscoursePart.discoursePart.dataSourceAggregate.sources.contains(dataSource);
		}
	}

}
