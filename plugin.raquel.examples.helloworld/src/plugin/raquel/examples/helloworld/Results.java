package plugin.raquel.examples.helloworld;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Results {

	protected Shell shell;
	private static Text results;
	static IPackageFragment[] packagesSelection;
	static Results window;
	static int NMCS = 0;

	public static void Inicializa(IPackageFragment[] p) {
		packagesSelection = p;
	}

	public static int extractInfixExpression (ASTNode node, int cont) {
		int k = 0;
		
		InfixExpression aux = (InfixExpression) node;
		
		results.append("\tNode: "+node.toString()
		+"\n\t\tLeft Side: "+aux.getLeftOperand().toString()
		+"\n\t\t\tType LeftSide: "+aux.getLeftOperand().getNodeType()
		+"\n\t\tRight Side: "+aux.getRightOperand().toString()
		+"\n\t\t\tType RightSide: "+	aux.getRightOperand().getNodeType()
		+"\n");
		
		if (aux.getLeftOperand().getNodeType() == 32) {
			results.append("\t\t\tLeftSide its MethodInvocation!\n");
			k = k + getChildren(aux.getLeftOperand(),0);
		}
		
		if (aux.getRightOperand().getNodeType() == 32) {
			results.append("\t\t\tRightSide its MethodInvocation!\n");
			k = k + getChildren(aux.getRightOperand(),0);	
		}
		
		return k;
	}
	
	public static int extractAssignment(Assignment node) {
		int k = 0;
		
		results.append("\tNode: "+node.toString()
		+"\n\t\tLeft Side: "+node.getLeftHandSide().toString()
		+"\n\t\t\tType LeftSide: "+node.getLeftHandSide().getNodeType()
		+"\n\t\tRight Side: "+node.getRightHandSide().toString()
		+"\n\t\t\tType RightSide: "+node.getRightHandSide().getNodeType()
		+"\n");
			
		if (node.getLeftHandSide().getNodeType() == 32){
			results.append("\t\t\tLeftSide its MethodInvocation!\n");
			k = getChildren(node.getLeftHandSide(),0);
			NMCS = NMCS + k;
			//return k;
		} 
			
		if (node.getRightHandSide().getNodeType() == 32) {
			results.append("\t\t\tRightSide its MethodInvocation!\n");
			k = getChildren(node.getRightHandSide(),0);
			NMCS = NMCS + k;
			//return k;
		}
		
		if (node.getRightHandSide().getNodeType() == 27) {
			k = extractInfixExpression(node.getRightHandSide(), 0);
			NMCS = NMCS + k;
			//return k;
		}
		
		return k;
	}
	
	public static int getChildren(ASTNode node,int n) {
		int cont = n;
		String compara = "[]";
		
	    List<ASTNode> children = new ArrayList<ASTNode>();
	    @SuppressWarnings("rawtypes")
		List list = node.structuralPropertiesForType();
	    
	    for (int i = 0; i < list.size(); i++) {
	        Object child = node.getStructuralProperty((StructuralPropertyDescriptor)list.get(i));
	        if (child instanceof ASTNode) {
	            children.add((ASTNode) child);
	        }
	    }
	    
	    String teste = children.toString();
	    //results.append("MethodInvocation Node: "+children.get(0).toString()+"\nNMCS: "+cont+"\n");
	    
	    // Se a string do filho for igual a [] -> CHEGOU AO FIM 
	    //e retorna resultado do contador para analyseClass
	    if (teste.equals(compara)) {
	    	results.append("\n---> NMCS = "+cont+"\n");
	    	return cont;
	    }
	    
	    // Aumenta o contador se o nó filho for MethodInvocation ou
	    //SuperMethodInvocation e lista seus métodos componentes, assim
	    //como parâmetros (se houver) de cada método encadeado
	    if (node.getNodeType() == 32) {
	    	cont++;
	    	MethodInvocation nodev = (MethodInvocation) node;
	    	results.append("\tMethodInvocation: "+nodev.getName()+"\n");
	    	// Lista parâmetros do MethodInvocation	    	
	    	if (nodev.arguments().toString().equals(compara) != true) {
	    		for (int k = 0; k < nodev.arguments().size(); k++) {
	    			results.append("\t\tArgument["+k+"]: "+nodev.arguments().get(k).toString()+"\n");
	    			// Verifica se parâmetro é método p/ poder incrementar cont e,
	    			//consequentemente, NMCS
	    			ASTNode param = (ASTNode) nodev.arguments().get(k);
	    			if (param.getNodeType() == 32) {
	    				cont++;
	    				results.append("\t\t\tArg["+k+"] its MethodInvocation!"
	    				+"\n\t\t\t\tNMCS -> "+cont+"\n");
	    			} else if (param.getNodeType() == 27) {
	    				results.append("\t\t\tArg["+k+"] its InfixExpression!\n");
	    				cont = cont + extractInfixExpression(param, cont); 
	    			}
	    		}
	    	}
	    } else if (node.getNodeType() == 48) {
	    	cont++;
	    	SuperMethodInvocation nodesv = (SuperMethodInvocation) node;
	    	results.append("\tSuperMethodInvocation: "+nodesv.getName()+"\n");
	    	// Lista parâmetros do SuperMethodInvocation	    	
	    	if (nodesv.arguments().toString().equals(compara) != true) {
	    		for (int k = 0; k < nodesv.arguments().size(); k++) {
	    			results.append("\t\tArgument["+k+"]: "+nodesv.arguments().get(k).toString()+"\n");
	    			// Verifica se parâmetro é método p/ poder incrementar cont e,
	    			//consequentemente NMCS
	    			ASTNode param = (ASTNode) nodesv.arguments().get(k);
	    			if (param.getNodeType() == 32) {
	    				cont++;
	    				results.append("\t\t\tArg["+k+"] its MethodInvocation!"
	    						+"\n\t\t\t\tNMCS -> "+cont+"\n");
	    			} else if (param.getNodeType() == 27) {
	    				results.append("\t\t\tArg["+k+"] its InfixExpression!\n");
	    				cont = cont + extractInfixExpression(param, cont); 
	    			}
	    		}
	    	}
	    }
	    
	    // Recursão para encontrar próximo nó (filho do filho)
		return getChildren(children.get(0),cont); 
}

	/**
	 * Analyse the class and take your nodes to search Message Chains
	 * 
	 * @param classe
	 * @throws JavaModelException
	 */
	 private void analyseClass(ICompilationUnit classe) throws JavaModelException {
		// ICompilationUnit unit == class
		// now create the AST for the ICompilationUnits
		CompilationUnit parse = parse(classe);

		
		VariableDeclarationStatementVisitor visitor0 = new VariableDeclarationStatementVisitor();
		parse.accept(visitor0);
		
		results.append("###########################################\n"
				+"##### VARIABLEDECLARATIONSTATEMENT LIST #####\n"
				+ "###########################################\n\n");
		// Write in the screen: VariableDeclarationStatement and your type
		for (VariableDeclarationStatement method : visitor0.getExpression()) {
			// Take variable declaration and converts to String, write in the screen
			String var = method.toString();
			
			results.append("VDS: "+var
					+"\tType: "+method.getType().toString()
					+"\n\tFragment: "+method.fragments().toString()
					+"\n\t\tSize: "+method.fragments().size()
					+"\n\t\tEmpty: "+method.fragments().isEmpty()
					+"\n\t\tIndex[0]: "+method.fragments().get(0)
					+"\n\tModifiers: "+method.modifiers().toString()
					+"\n\n");
		}
		
		// Calls the method for visit node in AST e return your information
		ExpressionStatementVisitor visitor = new ExpressionStatementVisitor();
		parse.accept(visitor);

		results.append("\n\n##################################\n"
				+ "##### EXPRESSIONSTATEMENTS LIST #####\n"
				+ "##################################\n\n");
		// Write in the screen: ExpressionStatement and your type
		for (ExpressionStatement method : visitor.getExpression()) {
			// Take expression and converts to String, write in the screen
			String t = null;
			t = method.getExpression().toString();
			
			// Analyze whether ExpressionStatement is MethodInvocation or SuperMethodInvocation
			// 32 -> METHOD_INVOCATION type
			if (method.getExpression().getNodeType() == 32) {
				results.append("MI: "+t+"\n");
				int j = getChildren(method,0);
				NMCS = NMCS+j;
				// Check if MethodInvocation is a Message Chain
				// MC > 2 (where 2 is the number of methods in a chain)
				if (j > 2) {
					results.append("---> NCMS > 2 então É Message Chain!\n__________________________________________\n");
				} else {
					results.append("---> NCMS <= 2 então NÃO É Message Chain!\n__________________________________________\n");
				}
				results.append("\n");
			}

			// 48 -> SUPER_METHOD_INVOCATION type
			if  (method.getExpression().getNodeType() == 48) {
				results.append("SMI: "+t+"\n");
				int j = getChildren(method,0);
				NMCS = NMCS+j;
				// Check if SuperMethodInvocation is a Message Chain
				// MC > 2 (where 2 is the number of methods in a chain)
				if (j > 2) {
					results.append("---> NMCS > 2 então É Message Chain!\n__________________________________________\n");
				} else {
					results.append("---> NMCS <= 2 então NÃO É Message Chain!\n__________________________________________\n");
				}
				results.append("\n");
			} 
			
			// 7 -> ASSIGNMENT type (a = a.getDataA().getDataB()...)
			if  (method.getExpression().getNodeType() == 7) {
				results.append("ASS: "+t+"\n\tType: "+method.getExpression().getNodeType()+"\n");
				Assignment aux = (Assignment) method.getExpression();
				int j = extractAssignment(aux);
				// Check if SuperMethodInvocation is a Message Chain
				// MC > 2 (where 2 is the number of methods in a chain)
				if (j > 2) {
					results.append("---> NMCS > 2 então É Message Chain!\n__________________________________________\n");
				} else {
					results.append("---> NMCS <= 2 então NÃO É Message Chain!\n__________________________________________\n");
				}
				results.append("\n");
			}

			// Imprime NMCS total da classe
			results.append("[CLASS NMCS = "+NMCS+"]\n\n");
		}
	}
	
	/**
	 * Reads a ICompilationUnit and creates the AST DOM for manipulating the
	 * Java source file
	 *
	 * @param unit
	 * @return
	 */
	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args, IPackageFragment[] p) {
		try {
			TesteDesign window = new TesteDesign();
			window.open(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open(IPackageFragment[] p) {
		Inicializa(p);
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(600, 600);
		shell.setText("SWT Application");
		shell.setLayout(null);

		Combo comboClasses = new Combo(shell, SWT.NONE);
		comboClasses.setBounds(107, 7, 387, 23);

		// Gera a lista de todas as classes do projeto selecionado
		// com o tipo IPackageFragment que obtenho todas as classes de um
		// projeto
		// IProject -> IPackageFragment -> ICompilationUnit -> File.java
		try {
			for (IPackageFragment mypackage : packagesSelection) {
				for (final ICompilationUnit compilationUnit : mypackage.getCompilationUnits()) {
					comboClasses.add(compilationUnit.getElementName());
					// results.append("## PACKAGE NAME: " +
					// mypackage.getElementName() + "\n");
					// results.append("## [CLASSE] COMPILATION UNIT NAME: " +
					// compilationUnit.getElementName() + "\n");
					// classSelection = compilationUnit;
					// analyseClass(compilationUnit);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		// deixa a primeira classe encontrada visível por default no combo
		comboClasses.select(0);

		Label lbl = new Label(shell, SWT.NONE);
		lbl.setBounds(10, 10, 91, 15);
		lbl.setText("Select the class:");

		Button btnApplyClass = new Button(shell, SWT.NONE);
		btnApplyClass.setBounds(500, 7, 75, 25);
		btnApplyClass.setText("Apply");

		results = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		results.setBounds(10, 49, 484, 334);

		Button btnReturn = new Button(shell, SWT.NONE);
		btnReturn.setText("Return");
		btnReturn.setBounds(500, 80, 75, 25);

		Button btnClear = new Button(shell, SWT.NONE);
		btnClear.setText("Clear");
		btnClear.setBounds(500, 49, 75, 25);

		btnApplyClass.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					// LIMPA A JANELA DOS RESULTADOS QUANDO SELECIONADO UMA NOVA
					// CLASSE
					results.setText("");
					// Libera memória do NMCS ao final da execução
					NMCS = 0;
					String nameClass = comboClasses.getItem(comboClasses.getSelectionIndex());

					for (IPackageFragment mypackage : packagesSelection) {
						for (final ICompilationUnit compilationUnit : mypackage.getCompilationUnits()) {
							String aux = compilationUnit.getElementName();
							if (aux.equals(nameClass)) {
								analyseClass(compilationUnit);
							}
						}
					}

				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
		});

		btnReturn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// Libera memória do NMCS ao final da execução
				NMCS = 0;
				shell.close();
			}
		});

		btnClear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// Libera memória do NMCS ao final da execução
				NMCS = 0;
				results.setText("");
			}
		});
	}
}
