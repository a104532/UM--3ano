using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using DataAccessLibrary.Interfaces;
using DataAccessLibrary.Models;

namespace DataAccessLibrary.Data
{
    public class EncomendasData : IEncomendasData
    {

        private readonly ISqlDataAccess _db;

        public EncomendasData(ISqlDataAccess db)
        {
            _db = db;
        }

        public Task<List<Encomenda>> GetAll()
        {
            string sql = "select * from dbo.Encomenda";

            return _db.LoadData<Encomenda, dynamic>(sql, new { });
        }

        public Task insertEncomenda(Encomenda encomenda)
        {
            string sql = @"insert into dbo.Encomenda (dataEntrega, estado, email, nrPasso, idProduto)
                           values (@dataEntrega, @estado, @email, @nrPasso, @idProduto);";

            return _db.saveData(sql, encomenda);
        }

        public async Task<List<Encomenda>> GetEncomendasCliente(string emailCliente)
        {
            string sql = @"select * from dbo.Encomenda where email = @emailCliente";

            return await _db.LoadData<Encomenda, dynamic>(sql, new { emailCliente });
        }

        public async Task<Encomenda> GetEncomenda(int id)
        {
            string sql = @"select * from dbo.Encomenda where idEncomenda = @id";
            var encomendas = await _db.LoadData<Encomenda, dynamic>(sql, new { id });
            if (encomendas.Count == 0)
            {
                return null;
            }
            return encomendas.FirstOrDefault();
        }

        public void UpdateEstadoEncomenda(int id, string estado)
        {
            string sql = @"update dbo.Encomenda set estado = @estado where idEncomenda = @id";
            _db.saveData(sql, new { estado, id });
        }

        public void UpdateNrPassoEncomenda(int id, int nrPasso)
        {
            string sql = @"update dbo.Encomenda set nrPasso = @nrPasso where idEncomenda = @id";
            _db.saveData(sql, new { nrPasso, id });
        }
    }
}
