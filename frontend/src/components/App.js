import React from 'react'
import '../css/App.css';
import Carga from './Carga';
import Busqueda from './Busqueda';

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      page: "busqueda"
    }
  }
  renderPage() {
    if (this.state.page == "carga") {
      return <Carga />
    } else if (this.state.page == "busqueda") {
      return <Busqueda />
    }
  }

  changePanel(panel) {
    this.setState({
      page: panel
    })
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
