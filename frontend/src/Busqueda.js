import React from 'react'
import './App.css';
import './Busqueda.css';
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

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
      e.target.style.backgroundColor = "yellow";
    }
    console.log(this.state.publicaciones);
  }
  getTitulo() {
    return this.tituloDiv.current.text;
  }
  getAutor() {
    return this.autorDiv.current.text;
  }
  buscar() {
    let autor = this.getAutor();
    let titulo = this.getTitulo();
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
