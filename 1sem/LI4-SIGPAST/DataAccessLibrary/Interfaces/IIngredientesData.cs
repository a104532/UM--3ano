using DataAccessLibrary.Models;

namespace DataAccessLibrary.Interfaces
{
    public interface IIngredientesData
    {
        Task<List<Ingrediente>> GetAll();
        Task<List<Ingrediente>> GetAllAsync();
        Task insertIngrediente(Ingrediente ingrediente);
        Task updateQuantidadeSimples(int idIngrediente, float novaQuantidade);
        Task updateQuantidade(int idPasso, int idProduto, List<IngredientePasso> ingredientesPasso, List <Ingrediente> todosIngredientes);
        Task<List<IngredientePasso>> GetIngredientesPasso(int idPasso, int idProduto);
    }
}