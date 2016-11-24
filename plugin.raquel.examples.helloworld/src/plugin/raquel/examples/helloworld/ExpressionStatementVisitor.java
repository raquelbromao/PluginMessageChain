package plugin.raquel.examples.helloworld;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ExpressionStatement;

public class ExpressionStatementVisitor extends ASTVisitor {
	List<ExpressionStatement> methods = new ArrayList<ExpressionStatement>();

	public boolean visit(ExpressionStatement node) {
		methods.add(node);
		return super.visit(node);
	}

	public List<ExpressionStatement> getExpression() {
		return methods;
	}
}