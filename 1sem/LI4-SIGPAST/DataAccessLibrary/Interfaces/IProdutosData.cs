using DataAccessLibrary.Models;

namespace DataAccessLibrary.Interfaces
{
    public interface IProdutosData
    {
        Task<List<Produto>> GetAll();
        Task insertProduto(Produto produto);
        Task<Produto> getProduto(int produtoId);
        Task removeProduto(int produtoId);
        Task<List<string>> getIngredientesProduto(int produtoId);
        Task<List<(int, Ingrediente)>> GetIngredientesEQuantidades(int produtoId);

        Task<int> getProdutoIdByName(string Designacao, string Descricao);

        Task InsertOrUpdateIngredienteProduto(int idIngrediente, int idProduto, float quantidade);
    }
}