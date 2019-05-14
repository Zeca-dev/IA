

public class Individuo{
	//Utilizando strings para representar os cromossomos facilitará nas conversões pois o tipo Integer tem método para essa conversão
	private String cromossomoX;
	private String cromossomoY;
	
	//usados como entrada para converter para número real dentro do intervalo passado [-1,1], ou outro qualquer
	private int x;
	private int y;
	
	private double realX; //Esses valores serão obtidos a partir da conversão de binários de um intervalo para um núemro real
	private double realY; 
	
	//Para uso da roleta
	private double avaliacao = 0; //guarda o resutado das avaliações
	private double probabilidade = 0; //guarda a probabilidade de ser sorteado na roleta
	
	//intervalo da roleta
	private int inicioRoleta=0;
	private int fimRoleta=0;
	
	

	public Individuo() {
		//Construtor vazio
	}
	
	
	
	
	public Individuo(int x, int y, int inf, int sup) {
		// Ao instanciar um indivíduo devemos preencher os atributos de seus cromossomos e por fim adicioná-lo na população
		this.x = x;   this.y = y;
		
		int k = AG.calculaBits(AG.precisao, inf, sup); //calcula o numero de bits necessários para atingir a precisão
		
		//A conversão para binário do tipo Integer não coloca os zeros à esqueda, por isso fiz a função ajustaBits 
		//para manter o tamanho dos cromossomos
		this.cromossomoX = this.ajustaBits(Integer.toBinaryString(x), k);
		this.cromossomoY = this.ajustaBits(Integer.toBinaryString(y), k);
		
		//Transforma os inteiros em números reais na faixa do intervalo passado (slides)
		this.realX = this.decimal(inf, sup, k, this.x);
		this.realY = this.decimal(inf, sup, k, this.y);
		
		
		AG.Populacao.add(this); //adiciona o indivíduo à população
		
	}


	 //recebe limites inferior e superior, k (quantudade de bits usados) e r (inteiro aleatório que cabe na faixa de k - (slides)
	public double decimal(int inf, int sup, int k, int r) {
		double real;
		
		real = inf + ( (sup - inf) / (Math.pow(2, k) - 1) ) * r;
		
		return real;
		
	}
	
	//recebe uma string que representa um binário e adiciona zeros para que essa string fique sempre com o tamanoh de bits necessários
	//para os cruzamentos e mutações
	public String ajustaBits(String bin, int k) {
		while (bin.length() < k) {
			bin = "0"+bin;
		}
		return bin;
	}

	public String getCromossomoX() {
		return cromossomoX;
	}



	public void setCromossomoX(String cromossomoX) {
		this.cromossomoX = cromossomoX;
	}



	public String getCromossomoY() {
		return cromossomoY;
	}



	public void setCromossomoY(String cromossomoY) {
		this.cromossomoY = cromossomoY;
	}

	



	public double getRealX() {
		return realX;
	}


	public void setRealX(double realX) {
		this.realX = realX;
	}


	public double getRealY() {
		return realY;
	}


	public void setRealY(double realY) {
		this.realY = realY;
	}


	public int getX() {
		return x;
	}



	public void setX(int x) {
		this.x = x;
	}



	public int getY() {
		return y;
	}



	public void setY(int y) {
		this.y = y;
	}
	
	public double getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(double avaliacao) {
		this.avaliacao = avaliacao;
	}

	public double getProbabilidade() {
		return probabilidade;
	}

	public void setProbabilidade(double probabilidade) {
		this.probabilidade = probabilidade;
	}
	
	

	public int getInicioRoleta() {
		return inicioRoleta;
	}

	public void setInicioRoleta(int inicioRoleta) {
		this.inicioRoleta = inicioRoleta;
	}

	public int getFimRoleta() {
		return fimRoleta;
	}

	public void setFimRoleta(int fimRoleta) {
		this.fimRoleta = fimRoleta;
	}


	
	
}
