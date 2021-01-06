import React from 'react'
import '../css/App.css';
import '../css/Busqueda.css';
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { requestDBSearch } from "../requests/SearchRequest";



class Busqueda extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      startDate: null,
      endDate: null,
      publicaciones: []
    }
    this.tituloDiv = React.createRef();
    this.autorDiv = React.createRef();
  }

  pickEndDate(date) {
    this.setState({
      endDate: date
    })
  }
  pickStartDate(date) {
    this.setState({
      startDate: date
    })
  }
  togglePublicacion(e, fuente) {
    if (this.state.publicaciones.includes(fuente)) {
      this.state.publicaciones.splice(this.state.publicaciones.indexOf(fuente), 1);
      e.target.style.backgroundColor = "transparent";
    } else {
      this.state.publicaciones.push(fuente);
      e.target.style.backgroundColor = "black";
    }
  }
  getTitulo() {
    return this.tituloDiv.current.value;
  }
  getAutor() {
    return this.autorDiv.current.value;
  }

  toggleAutor() {
    return !this.getAutor() == "";
  }

  async buscar() {
    this.props.toggleSearchByAutor(this.toggleAutor());
    let autor = this.getAutor().trim();
    if (autor === undefined || autor == "") {
      autor = "%20";
    }
    let titulo = this.getTitulo().trim();
    if (titulo === undefined || titulo == "") {
      titulo = "%20";
    }
    let startYear = this.state.startDate != null ? this.state.startDate.getFullYear() : "0";
    let endYear = this.state.endDate != null ? this.state.endDate.getFullYear() : "0";
    let articulo = this.state.publicaciones.includes("articulo") ? "true" : "false";
    let congreso = this.state.publicaciones.includes("congreso") ? "true" : "false";
    let libro = this.state.publicaciones.includes("libro") ? "true" : "false";
    let results = await requestDBSearch(titulo, autor, startYear, endYear, articulo, congreso, libro);
    this.props.onSearch(results);
  }
  cancelar() {

  }
  render() {
    return (
      <div className="busqueda">
        <div className="title">Búsqueda bibliográfica de IEI</div>
        <hr className="line"></hr>
        <div className="fuentes-datos">
          <div className="title-fuente">Fuentes de datos</div>
          <div className="fuentes-datos-elecciones">
            <div className="fuente-dato">
              <div className="fuente-dato-tick" onClick={e => this.togglePublicacion(e, "articulo")}></div>
              <div className="fuente-dato-name">articulo</div>
            </div>
            <div className="fuente-dato">
              <div className="fuente-dato-tick" onClick={e => this.togglePublicacion(e, "libro")}></div>
              <div className="fuente-dato-name">libro</div>
            </div>
            <div className="fuente-dato">
              <div className="fuente-dato-tick" onClick={e => this.togglePublicacion(e, "congreso")}></div>
              <div className="fuente-dato-name">comunicación en congreso</div>
            </div>
          </div>
        </div>
        <div className="title-fuente" style={{ marginTop: "20px" }}>Buscar referencias por</div>
        <div className="fields">
          <input className="input" ref={this.autorDiv} placeholder="Autor..."></input>
          <input className="input" ref={this.tituloDiv} placeholder="Título..."></input>
          <div className="año">
            <div className="año-nombre">Fecha inicio</div>
            <DatePicker
              selected={this.state.startDate}
              dateFormat="dd/MM/yy"
              onChange={date => this.pickStartDate(date)}
              date={this.state.startDate}
            >
            </DatePicker>
          </div>
          <div className="año">
            <div className="año-nombre">Fecha fin</div>
            <DatePicker
              selected={this.state.endDate}
              dateFormat="dd/MM/yy"
              onChange={date => this.pickEndDate(date)}
              date={this.state.endDate}
            >
            </DatePicker>
          </div>
        </div>
        <div className="botones-formularios">
          <button className="boton-formulario" onClick={() => this.buscar()}>Buscar</button>
          <button className="boton-formulario" onClick={() => this.cancelar()}>Cancelar</button>
        </div>
      </div>
    );
  }
}

export default Busqueda;
