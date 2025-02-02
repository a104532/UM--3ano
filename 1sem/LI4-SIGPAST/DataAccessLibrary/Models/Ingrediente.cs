namespace DataAccessLibrary.Models
{
    public class Ingrediente
    {
        public int idIngrediente {  get; set; }
        public string designacao {  get; set; }
        public string unidade { get; set; }
        public float quantidadeStock { get; set; }
        public string imagem { get; set; }

        public Ingrediente() { }

       

        public Ingrediente(int idIngrediente,string designacao, string unidade, int quantidadeStock, string imagem)
        {
            this.idIngrediente = idIngrediente;
            this.designacao = designacao;
            this.unidade = unidade;
            this.quantidadeStock = quantidadeStock;
            this.imagem = imagem;
        }

        public float QuantidadeARemover(int quantidadeReceita)
        {
            if (this.unidade.Equals("unidades"))
            {
                return (float)quantidadeReceita;
            }
            else if (this.unidade.Equals("kg") || this.unidade.Equals("l"))
            {
                return (float)quantidadeReceita / 1000;
            }
            return (float)quantidadeReceita / 1000;
        }

        public string UnidadeMenor()
        {
            if (this.unidade.Equals("unidades"))
            {
                return "unidades";
            }
            else if (this.unidade.Equals("kg"))
            {
                return "g";
            }
            else if (this.unidade.Equals("l"))
            {
                return "ml";
            }
            return "g";
        }
    }
}
