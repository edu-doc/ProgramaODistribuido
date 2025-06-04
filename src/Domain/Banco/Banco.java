package Domain.Banco;

import java.util.List;

public class Banco<T> {

	private int quantidadeNo;

	EscritorLog escritor = new EscritorLog("src/Log.txt");

	public int getQuantidadeNo(){
		return quantidadeNo;
	}

	No<T> raiz = null;

	public Banco() {
		quantidadeNo = 0;
	}

	public void ordem() {
		this.ordem(raiz);
	}

	private void ordem(No<T> arv) {

		if (arv != null) {
			this.ordem(arv.esq);
			System.out.print(arv.dado.toString() + " ");
			this.ordem(arv.dir);
		}

	}

	public List<T> listar() {
		List<T> todosElementos = null;
		this.listar(raiz,todosElementos);
		return todosElementos;
	}

	private void listar(No<T> arv, List<T> todosElementos ) {

		if (arv != null) {
			this.listar(arv.esq, todosElementos);
			todosElementos.add(arv.dado);
			this.listar(arv.dir, todosElementos);
		}

	}

	public void inserir(int chave, T dado) {
		escritor.escreverLog("=== Procedimento de inserir ===");
		raiz = inserir(raiz, chave, dado);
		escritor.escreverLog("Altura pós inserir: " + alturaArv());
		quantidadeNo++;
	}

	private No<T> inserir(No<T> arv, int chave, T dado) {

		if (arv == null)
			return new No<T>(chave, dado);

		if (chave < arv.chave)
			arv.esq = inserir(arv.esq, chave, dado);

		else if (chave > arv.chave)
			arv.dir = inserir(arv.dir, chave, dado);

		else
			return arv;

		arv.alturaNo = 1 + maior(altura(arv.esq), altura(arv.dir));
		int fb = obterFB(arv);
		int fbSubArvEsq = obterFB(arv.esq);
		int fbSubArvDir = obterFB(arv.dir);

		if (fb > 1 && fbSubArvEsq >= 0) {

			return rds(arv);

		}

		if (fb < -1 && fbSubArvDir <= 0) {

			return res(arv);

		}

		if (fb > 1 && fbSubArvEsq < 0) {
			// RDD
			arv.esq = res(arv.esq);
			return rds(arv);

		}

		if (fb < -1 && fbSubArvDir > 0) {
			// RDE
			arv.dir = rds(arv.dir);
			return res(arv);

		}

		return arv;

	}

	public void remover(int chave, T dado) {
		escritor.escreverLog("=== Procedimento de remover ===");
		raiz = remover(raiz, chave, dado);
		escritor.escreverLog("Altura pós remover: " + alturaArv());
		quantidadeNo--;
	}

	private No<T> remover(No<T> arv, int chave, T dado) {

		if (arv == null)
			return arv;

		if (chave < arv.chave)
			arv.esq = remover(arv.esq, chave, dado);

		else if (chave > arv.chave)
			arv.dir = remover(arv.dir, chave, dado);

		else {

			if (arv.esq == null && arv.dir == null)

				arv = null;

			else if (arv.esq == null) {

				No<T> temp = arv;
				arv = temp.dir;
				temp = null;
			}

			else if (arv.dir == null) {

				No<T> temp = arv;
				arv = temp.esq;
				temp = null;
			} else {

				No<T> temp = menorChave(arv.dir);
				arv.chave = temp.chave;
				arv.dado = temp.dado;
				temp.chave = chave;
				temp.dado = dado;
				arv.dir = remover(arv.dir, temp.chave, temp.dado);

			}

		}

		if (arv == null)
			return arv;

		arv.alturaNo = 1 + maior(altura(arv.esq), altura(arv.dir));
		int fb = obterFB(arv);
		int fbSubArvEsq = obterFB(arv.esq);
		int fbSubArvDir = obterFB(arv.dir);

		if (fb > 1 && fbSubArvEsq >= 0) {

			return rds(arv);

		}

		if (fb < -1 && fbSubArvDir <= 0) {

			return res(arv);

		}

		if (fb > 1 && fbSubArvEsq < 0) {

			arv.esq = res(arv.esq);
			return rds(arv);

		}

		if (fb < -1 && fbSubArvDir > 0) {

			arv.dir = rds(arv.dir);
			return res(arv);

		}

		return arv;

	}

	private No<T> menorChave(No<T> arv) {

		No<T> temp = arv;

		if (temp == null)
			return null;

		while (temp.esq != null)
			temp = temp.esq;

		return temp;

	}

	private No<T> rds(No<T> y) {

		escritor.escreverLog("Rotação a Direta Executada");

		No<T> x = y.esq;
		No<T> z = x.dir;

		x.dir = y;
		y.esq = z;

		y.alturaNo = maior(altura(y.esq), altura(y.dir) + 1);
		x.alturaNo = maior(altura(x.esq), altura(x.dir) + 1);

		return x;

	}

	private No<T> res(No<T> x) {

		escritor.escreverLog("Rotação a Esquerda Executada");

		No<T> y = x.dir;
		No<T> z = y.esq;

		y.esq = x;
		x.dir = z;

		x.alturaNo = maior(altura(x.esq), altura(x.dir) + 1);
		y.alturaNo = maior(altura(y.esq), altura(y.dir) + 1);

		return y;

	}

	private int obterFB(No<T> arv) {
		if (arv == null)
			return 0;

		return altura(arv.esq) - altura(arv.dir);

	}

	private int maior(int a, int b) {

		return (a > b) ? a : b;

	}

	private int altura(No<T> arv) {

		if (arv == null)
			return -1;

		return arv.alturaNo;
	}

	public int altura() {
		return altura(raiz);
	}

	public boolean existe (int codigo) {
		No<T> no = buscar(raiz, codigo);
		if(no == null) {
			return false;
		} else {
			return true;
		}
	}

	public T buscarDado(int codigo) {
		No<T> no = buscar(raiz, codigo);
		return no.getDado();
	}

	public No<T> buscar(int codigo) {
		return  buscar(raiz, codigo);
	}

	private No<T> buscar(No<T> arv, int codigo) {
		if (arv == null || arv.chave == codigo) {
			return arv;
		}

		if (codigo < arv.chave) {
			return buscar(arv.esq, codigo);
		} else {
			return buscar(arv.dir, codigo);
		}
	}

	public int alturaArv(){
		return altura(raiz);
	}


}