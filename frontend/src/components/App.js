import React, { useDebugValue } from 'react'
import '../css/App.css';
import Carga from './Carga';
import Busqueda from './Busqueda';
import Resultados from './Resultados';
import ResultadoCarga from './ResultadoCarga';

const testData = {
  "articulos": [
    {
      "autores": ["paquito salas", "manuel alvado"],
      "titulo": "titulo irrelevante xd",
      "revista": "revista jeje",
      "volumen": 2,
      "numero": 3,
      "mes": 3,
      "pagina-inicio": 12,
      "pagina-fin": 15,
      "URL": "peredb.com"
    }
  ],
  "libros": [
    {
      "autores": ["paquito salas", "manuel alvado"],
      "titulo": "titulo irrelevante xd",
      "editorial": "editorial ginebra",
      "año": 1999,
      "URL": "peredb.com"
    }

  ],
  "congresos": [
    {
      "autores": ["paquito salas", "manuel alvado"],
      "titulo": "titulo irrelevante xd",
      "revista": "revista jeje",
      "edicion": 2,
      "congreso": "congreso pringao",
      "lugar": "albacete",
      "pagina-inicio": 12,
      "pagina-fin": 15,
      "URL": "peredb.com"
    }

  ]
}

const testDataLoad = {
  "dblp": 123,
  "ieeex": 12,
  "scholar": 200,
}

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      page: "busqueda",
      resultPage: 1,
      searchResults: [],
      loadTotals: undefined,
      searchByAutor: false,
    }
  }
  renderPage() {
    if (this.state.page == "carga") {
      return <Carga
        onLoad={(totals) => this.load(totals)}
      />
    } else if (this.state.page == "busqueda") {
      return <Busqueda onSearch={(results) => this.search(1, results)}
        toggleSearchByAutor={(res) => this.toggleSearchByAutor(res)} />
    } else if (this.state.page == "resultados") {
      return <Resultados
        searchByAutor={this.state.searchByAutor}
        data={this.state.searchResults}
        page={this.state.resultPage}
        onChangeResultPage={(page) => this.search(page)}
      />
    } else if (this.state.page == "resultadosCarga") {
      return <ResultadoCarga
        onExit={() => this.changePanel("busqueda")}
        totals={this.state.loadTotals}
      />
    }
  }

  changePanel(panel) {
    this.setState({
      page: panel
    })
  }

  search(page, results) {
    console.log(page);
    this.setState({
      resultPage: page,
      searchResults: results,
      page: "resultados"
    });
  }

  toggleSearchByAutor(searchByAutor) {
    this.setState({
      searchByAutor: searchByAutor,
    })
  }

  load(totals) {
    this.setState({
      loadTotals: totals,
      page: "resultadosCarga",
    });
  }

  render() {
    return (
      <div className="App">
        <div className="nav">
          <button className="nav-button" onClick={() => this.changePanel("carga")}>Carga</button>
          <button className="nav-button" onClick={() => this.changePanel("busqueda")}>Busqueda</button>
        </div>
        {this.renderPage()}
      </div>
    );
  }
}

export default App;
