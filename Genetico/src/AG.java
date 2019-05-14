import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import javax.sound.midi.Soundbank;

public class AG {
	
	public static double precisao;
	public static int inf; //limite inferior
	public static int sup; //limite superior
	public static int k; //numero de bits necess�rios para a precis�o exigida
	public static int Geracao = 0; //Conta as novas gera��es
	
	
	
	public static ArrayList<Individuo> Populacao = new ArrayList<>(); //Popula��o de individuos
	public static ArrayList<Individuo> Filhos = new ArrayList<>(); //Filhos gerados a cada itera��o - nova gera��o
	
	public static double TotalAvaliacoes = 0; //Usada para o c�lculo das probabilidades da roleta
	
	public static Individuo Melhor; //Ser� verificado a cada x itera��es para avaliar a efic�cia do algoritmo
	public static int g = Geracao; //gera��o do melhor individuo
	//Crit�rio de parada. Se o melhor ficar velho, h� grande chance de n�o econtrarmos outro indiv�duo melhor, ent�o devemos parar o algor�tmo
	public static int idade = 0; 
								

	public AG() {
		// TODO Auto-generated constructor stub
	}
	
	//calcula a quantidade de bits necess�rios para a precis�o exigida
		public static int calculaBits (double precisao, int inf, int sup) {
			double k = 0;
			double result= 1;
			result = ( (sup - inf) / precisao ) + 1;
			while (Math.pow(2, k) <= result ) {
				
				k = k+0.01;
			}
		
			if ( result -  Math.pow(2, k) > 10) {
				k = k +1;
			}
			return (int)Math.round(k); //arredonda e retorna k
		}
		
	
	//recebe o tamanho desejado e cria uma popula��o com indiv�duos, aleatoriamente, com valores dentro da faixa de bits k
	public static void geraPopulacao(int tam) {
		Random gerador = new Random();
		
		for(int i= 0; i < tam; i++) {
			Individuo ind = new Individuo(gerador.nextInt((int)Math.pow(2, AG.k)), gerador.nextInt((int)Math.pow(2, AG.k)), AG.inf, AG.sup); 
		}
		
	}

	//Recebe a popula��o, passa na fun��o de avalia��o e seleciona o melhor o retornando para o cruzamento, baseada na precis�o exigida
	public static void avaliar() {
		
		/*Dividi a fun��o de avalia��o em duas partes, a do m�dulo e a da soma. Observa��o: inverter os valores de X e Y para a fun��o, j� que
		  pretende minimizar
		*/
		double modulo; //m�odulo
		double funcAval; //fun��o de avalia��o;

		for(int i=0; i < AG.Populacao.size(); i++) {
			modulo = Math.exp(AG.Populacao.get(i).getRealX()) - Math.pow(AG.Populacao.get(i).getRealY() , 2 ) + 1; //parte do mod�lo
			
			if (modulo < 0) {
				modulo = modulo * (-1);
			}
			
			funcAval = modulo + Math.pow(10,-4); //parte final da fun��o, j� com o m�dulo
			
			//para o c�lculo das probabildades da roleta
			AG.Populacao.get(i).setAvaliacao( funcAval);

			AG.TotalAvaliacoes =  AG.TotalAvaliacoes + (1 / funcAval); //Estou guardando a soma dos inversos das avalia��es
			
	
		}
		
		//Calcula a probabilidade de cada individuo
		for(int i=0; i < AG.Populacao.size(); i++) {
			AG.Populacao.get(i).setProbabilidade( ( (1/AG.Populacao.get(i).getAvaliacao() ) / AG.TotalAvaliacoes) * 100);
		}
		
		ajustaProbabilidades();
		
	}
	
	//ajusta Probabilidades (n�o pode passar de  100%)
	public static void ajustaProbabilidades() {
		double Percentual = 0;
		double difPercentual = 0;	
		
		for(int i=0; i < AG.Populacao.size(); i++) {
			Percentual = Percentual + AG.Populacao.get(i).getProbabilidade();
		}
		
		
		difPercentual = Percentual - 100;
		if (difPercentual > 0) {
			difPercentual = difPercentual / AG.Populacao.size(); //divide a diferen�a pela popula��o
			
			for(int i=0; i < AG.Populacao.size(); i++) {
				AG.Populacao.get(i).setProbabilidade( AG.Populacao.get(i).getProbabilidade() - difPercentual );	//ajusta percentual dos indiv�duos
			}
		}else if (difPercentual < 0) {
			difPercentual = (difPercentual / AG.Populacao.size()) *(-1); //divide a diferen�a pela popula��o

			for(int i=0; i < AG.Populacao.size(); i++) {
				AG.Populacao.get(i).setProbabilidade( AG.Populacao.get(i).getProbabilidade() + difPercentual );	//ajusta percentual dos indiv�duos
			
			}
		}
	}
		
	//Seleciona individuos pais
	public static Individuo selecionaPai() {
		Individuo pai;
		Random gerador = new Random();
		int roleta = gerador.nextInt(360); //Gera um n�mero de 0 a 360
		
		
		int fimAnterior=0;
		
		for(int i=0; i < AG.Populacao.size(); i++) {
			
			int fatia = (int) Math.round(360 * AG.Populacao.get(i).getProbabilidade() / 100);
			
			AG.Populacao.get(i).setInicioRoleta(fimAnterior);
			AG.Populacao.get(i).setFimRoleta(fimAnterior + fatia);
			fimAnterior = fimAnterior + fatia;	
		}
		
		//Pega o pai e remove da popula��o
		
		if(AG.Populacao.size() == 1) {
			pai = AG.Populacao.get(0);
			AG.Populacao.remove(0);
			
			return pai;
		}
		
		for(int i=0; i < AG.Populacao.size(); i++) {
			if(roleta > AG.Populacao.get(i).getInicioRoleta()  && roleta <= AG.Populacao.get(i).getFimRoleta()) {
				pai = AG.Populacao.get(i);
				AG.Populacao.remove(i);
				AG.TotalAvaliacoes = AG.TotalAvaliacoes - (1 / pai.getAvaliacao());
				
				return pai;
			}
			
		}
		
		
		
		return null;
	}
	
	//recebe dois individuos, faz o cruzamento e guarda na lista de filhos.
	public static void cruzamento(Individuo ind1, Individuo ind2) {
		
		Individuo filho1 = new Individuo(); //cria individuo e atribui caracteristicas dos pais
		Individuo filho2 = new Individuo();
		
		//parte esquerda do primeiro pai + parte direita do segundo pai
		filho1.setCromossomoX( ind1.getCromossomoX() ); 
		filho1.setCromossomoY( ind2.getCromossomoY() ); 
		
		//parte esquerda do segundo pai + parte direita do primeiro pai
		filho2.setCromossomoX( ind2.getCromossomoX() );
		filho2.setCromossomoY( ind1.getCromossomoY() );

		//Aplica a muta��o - Crit�rio 5% de probabilidade
		Random gerador = new Random();
		int n = gerador.nextInt(100); //Gera um n�mero de 0 a 100. 
		
		if (n < 5) {
			filho1.setCromossomoY(mutacao(filho1));
			filho2.setCromossomoY(mutacao(filho2));	
		}
		
	
		//Os demais valores dos filhos ser�o preenchidos transformando esses cromossomos bin�rios em X e Y inteiros
		
		filho1.setX(Integer.parseInt(filho1.getCromossomoX(), 2));
		filho1.setY(Integer.parseInt(filho1.getCromossomoY(), 2));
		filho1.setRealX(filho1.decimal(AG.inf, AG.sup, AG.k, filho1.getX()));
	    filho1.setRealY(filho1.decimal(AG.inf, AG.sup, AG.k, filho1.getY()));
	    
	    filho2.setX(Integer.parseInt(filho2.getCromossomoX(), 2));
		filho2.setY(Integer.parseInt(filho2.getCromossomoY(), 2));
		filho2.setRealX(filho2.decimal(AG.inf, AG.sup, AG.k, filho2.getX()));
	    filho2.setRealY(filho2.decimal(AG.inf, AG.sup, AG.k, filho2.getY()));
	    
	    Filhos.add(filho1); Filhos.add(filho2); //Adiciona os filhos na lista de fillhos
	    
	}
	
	//recebe um individuo e faz uma muta��o no mesmo
	public static String mutacao(Individuo filho) {
		StringBuffer y = new StringBuffer(filho.getCromossomoY());
		
		if(y.toString().substring(0, 0) == "0") {
			y.setCharAt(0, '1');			
		}else {
			y.setCharAt(0, '0');	
		}
		
		return y.toString();
	}
	
	//Pega o melhor individuo da popula��o atual
	public static Individuo pegaMelhor() {
		Individuo melhor = AG.Populacao.get(0);
		for(Individuo i: AG.Populacao) {
			if(i.getAvaliacao() < melhor.getAvaliacao()) { //peguei a menor avalia��o pq o problema � de minimizar
				melhor = i;
			}
		}
		return melhor;		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		inf = -1; sup = 1; //limites para o problema
		precisao = 0.0005; //precis�o
		
		//calcula bits
		k = calculaBits(precisao, inf, sup);
		
		
		geraPopulacao(50); 
		avaliar();
		
		
		
		AG.Melhor = pegaMelhor(); //guarda o melhor individuo
				

		while (true) { 
			
			if(AG.idade > 10) { //Se o individuo ficar velho e n�o aparecer ningu�m melhor que ele
				AG.idade--; //ajusta idade
				System.out.println("\n --- Fim --- \nTotal de Gera��es: "+AG.Geracao+"\n");
				System.out.println("Resposta: [Gera��o = "+AG.g+"  Idade "+AG.idade+"] => Indiv�duo: " 									+AG.Melhor.getCromossomoX()+AG.Melhor.getCromossomoY() + " - f("+AG.Melhor.getRealX()+" ," 									+AG.Melhor.getRealY()+") = " +AG.Melhor.getAvaliacao() );
			
				break;
			} 
			
			Individuo pai1 = null; 
			Individuo pai2 = null;
			 
			while (pai1 == null) {
				pai1 = selecionaPai();
			}
			
			while (pai2 == null) {
				pai2 = selecionaPai();
			}
			
			cruzamento(pai1, pai2);
			
			
			
			
			if (AG.Populacao.size() == 0) {
				
				for (Individuo filho : Filhos) { //A nova popula��o ser� os filhos gerados
					
					Populacao.add(filho);
				}
			
				Filhos.clear();
				AG.TotalAvaliacoes = 0;
				AG.Geracao++;
				avaliar();
				
				//Mostra dados
				System.out.println("\n Gera��o: "+AG.Geracao + "   Tamanho da popula��o: "+AG.Populacao.size()+"\n");
				for (Individuo i: AG.Populacao) {
					System.out.println("Individuo: "+i.getCromossomoX()+i.getCromossomoY() + " - f("+i.getRealX()+" , "+i.getRealY()+") = " +i.getAvaliacao() +" - Prob: "+i.getProbabilidade());
				}
				
				
				//guarda o melhor indiv�duo a cada 3 gera��es
				
					Individuo melhor = pegaMelhor(); //pega o melhor dessa gera��o e abaixo compara com o melhor antigo
					
					if (melhor.getAvaliacao() <= AG.Melhor.getAvaliacao()) { //melhor avalia��o (mais pr�ximo de zero no caso)
						AG.Melhor = melhor;
						AG.g = AG.Geracao;
						AG.idade = 0;
					}				
					AG.idade++;

			}
			
				
			
		}		
		
	}

}
