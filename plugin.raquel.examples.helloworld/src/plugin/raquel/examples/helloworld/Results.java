package plugin.raquel.examples.helloworld;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
//import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Results {

	protected Shell shell;	
	private static Text results;
	static IPackageFragment[] packagesSelection;
	static Results window;

	public static void Inicializa(IPackageFragment[] p) {
		packagesSelection = p;
	}
	
	public static void splitMessageChain(String s) {
		// retira o ";" do final da string
		s = s.replace(";", " ");

		// Quebra a variável quando acha . e armazena a sobra numa posição do
		// array aux
		// a().b() -> . é descartando e a() fica em aux[0] e b() em aux[1]
		String[] aux = s.split(Pattern.quote("."));

		// Pega o tamanho da string aux
		// Imprime a variável aux na tela
		results.append("Objeto: " + aux[0] + "\n");
		for (int i = 1; i < aux.length; i++) {
			results.append("Método[" + i + "]: " + aux[i] + "\n");
		}

		results.append("_______________________________________________________\n");
	}

	public static void verificaMessageChain(String s) {
		// verifica se a expressão coletada é igual ao regex criado
		// não foi usado [;] no final do regex pq o compilador nem lê se não
		// houver ele no final
		if (s.matches("[\\w]+([\\.]+[\\w]+[(]+[)]){2,}")) { // "[\\w]+([\\.]+[\\w]+[(]+[?\\w]+[)]){2,}")
			results.append("\nMessage Chain: " + s + "\n");
			splitMessageChain(s);
		} else {
			results.append("\nNão é Message Chain: " + s + "\n_______________________________________________________\n");
		}
	}

	private void analyseClass(ICompilationUnit classe) throws JavaModelException {
		// ICompilationUnit unit = classe;
		// now create the AST for the ICompilationUnits
		CompilationUnit parse = parse(classe);
		ExpressionInvoke visitor = new ExpressionInvoke();
		parse.accept(visitor);

		// Imprime na tela o nome do método e o tipo de retorno
		for (ExpressionStatement method : visitor.getMethods()) {
			String t = null;
			t = method.getExpression().toString();

			verificaMessageChain(t);
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
		// IProject -> IPackageFragment -> ICompilationUnit -> arq.java
		try {
			for (IPackageFragment mypackage : packagesSelection) {
				for (final ICompilationUnit compilationUnit : mypackage.getCompilationUnits()) {
					comboClasses.add(compilationUnit.getElementName());
					// results.append("## PACKAGE NAME: " + mypackage.getElementName() + "\n");
					// results.append("## [CLASSE] COMPILATION UNIT NAME: " + compilationUnit.getElementName() + "\n");
					// classSelection = compilationUnit;
					// analyseClass(compilationUnit);
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
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
					// LIMPA A JANELA DOS RESULTADOS QUANDO SELECIONADO UMA NOVA CLASSE
					results.setText("");
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		btnReturn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		btnClear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				results.setText("");
			}
		});

		
	}
}
