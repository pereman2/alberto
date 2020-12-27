import React from 'react'
import '../css/App.css';
import Carga from './Carga';
import Busqueda from './Busqueda';
import Resultados from './Resultados';

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

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      page: "resultados",
      resultPage: 1
    }
  }
  renderPage() {
    if (this.state.page == "carga") {
      return <Carga />
    } else if (this.state.page == "busqueda") {
      return <Busqueda />
    } else if (this.state.page == "resultados") {
      return <Resultados
        data={testData}
        page={this.state.resultPage}
        onChangeResultPage={(page) => this.search(page)}
      />
    }
  }

  changePanel(panel) {
    this.setState({
      page: panel
    })
  }

  search(page) {  
    console.log(page);
    this.changePanel("resultados");
    this.setState({
      resultPage: page
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
