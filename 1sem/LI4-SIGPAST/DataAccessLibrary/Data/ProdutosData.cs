using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using DataAccessLibrary.Interfaces;
using DataAccessLibrary.Models;

namespace DataAccessLibrary.Data
{
    public class ProdutosData : IProdutosData
    {
        private readonly ISqlDataAccess _db;

        public ProdutosData(ISqlDataAccess db)
        {
            _db = db;
        }

        public Task<List<Produto>> GetAll()
        {
            string sql = "select * from dbo.Produto";

            return _db.LoadData<Produto, dynamic>(sql, new { });
        }

        public async Task<Produto> getProduto(int produtoId)
        {
            string sql = @"select * from dbo.Produto
                           where idProduto = @produtoId;";

            List<Produto> u = await _db.LoadData<Produto, dynamic>(sql, new { produtoId });

            if (u.Count > 0)
                return u[0];
            else
                return null;
        }

        public async Task insertProduto(Produto produto)
        {
            string sql = @"insert into dbo.Produto (designacao, descricao, imagem, nrPassos)
                           values (@designacao, @descricao, @imagem, @nrPassos);";

            await _db.saveData(sql, produto);
        }

        public async Task removeProduto(int produtoId)
        {
            string sql = @"delete from dbo.IngredienteProduto
                           where idProduto = @produtoId;

                            delete from dbo.IngredientePassoProducao
                           where idProduto = @produtoId;

                            delete from dbo.PassoProducao
                           where idProduto = @produtoId;

                           delete from dbo.Produto 
                           where idProduto = @produtoId;";

            await _db.saveData(sql, new { produtoId });
        }

        public async Task<List<string>> getIngredientesProduto(int produtoId)
        {
            string sql = @"select i.designacao from dbo.Ingrediente i
                           join dbo.IngredienteProduto pi on i.idIngrediente = pi.idIngrediente
                           where pi.idProduto = @produtoId;";

            return await _db.LoadData<string, dynamic>(sql, new { produtoId });
        }

        public async Task<List<(int, Ingrediente)>> GetIngredientesEQuantidades(int produtoId)
        {
            string sql = @"select pi.quantidade, i.*
                   from dbo.Ingrediente i
                   join dbo.IngredienteProduto pi on i.idIngrediente = pi.idIngrediente
                   where pi.idProduto = @produtoId;";

            var rawData = await _db.LoadData<dynamic, dynamic>(sql, new { produtoId });

            return rawData.Select(row =>
                ((int)row.quantidade,
                new Ingrediente((int)row.idIngrediente,(string)row.designacao,(string)row.unidade,(int)row.quantidadeStock,(string)row.imagem)))
                .ToList();
        }

        public async Task<int> getProdutoIdByName(string Designacao, string Descricao)
        {
            string sql = @"select idProduto from dbo.Produto
                           where designacao = @Designacao and descricao = @Descricao;";

            List<int> u = await _db.LoadData<int, dynamic>(sql, new { Designacao, Descricao });

            if (u.Count > 0)
                return u[0];
            else
                return -1;
        }

        public async Task InsertOrUpdateIngredienteProduto(int idIngrediente, int idProduto, float quantidade)
        {
            string sql = @"merge dbo.IngredienteProduto as target
                        using (values (@idIngrediente, @idProduto, @quantidade)) as source (idIngrediente, idProduto, quantidade)
                        on target.idIngrediente = source.idIngrediente and target.idProduto = source.idProduto
                        when matched then
                        update set target.quantidade = target.quantidade + source.quantidade
                        when not matched then
                        insert (idIngrediente, idProduto, quantidade)
                        values (source.idIngrediente, source.idProduto, source.quantidade);";

            await _db.saveData(sql, new { idIngrediente, idProduto, quantidade });
        }
    }
}
