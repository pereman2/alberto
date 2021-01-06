import React from 'react'
import '../css/Carga.css';
import '../css/App.css';
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { requestDBLoad } from "../requests/SearchRequest";


class Carga extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            startDate: null,
            endDate: null,
            fuentes: [],
        }
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

    toggleFuente(e, fuente) {
        if (this.state.fuentes.includes(fuente)) {
            this.state.fuentes.splice(this.state.fuentes.indexOf(fuente), 1);
            e.target.style.backgroundColor = "transparent";
        } else {
            this.state.fuentes.push(fuente);
            e.target.style.backgroundColor = "black";
        }
        console.log(this.state.fuentes);
    }
    async cargar() {
        let startYear = this.state.startDate != null ? this.state.startDate.getFullYear() : "0";
        let endYear = this.state.endDate != null ? this.state.endDate.getFullYear() : "0";
        let dblp = this.state.fuentes.includes("dblp") ? "true" : "false";
        let iex = this.state.fuentes.includes("iex") ? "true" : "false";
        let scholar = this.state.fuentes.includes("scholar") ? "true" : "false";
        let totals = await requestDBLoad(startYear, endYear, dblp, iex, scholar);
        this.props.onLoad(totals);
    }
    cancelar() {

    }
    render() {
        return (
            <div className="carga">
                <div className="title">Carga del almacén de datos</div>
                <hr className="line"></hr>
                <div className="fuentes-datos">
                    <div className="title-fuente">Fuentes de datos</div>
                    <div className="fuentes-datos-elecciones">
                        <div className="fuente-dato">
                            <div className="fuente-dato-tick" onClick={e => this.toggleFuente(e, "dblp")}></div>
                            <div className="fuente-dato-name">DBLP</div>
                        </div>
                        <div className="fuente-dato">
                            <div className="fuente-dato-tick" onClick={e => this.toggleFuente(e, "iex")}></div>
                            <div className="fuente-dato-name">IEEE Xplore</div>
                        </div>
                        <div className="fuente-dato">
                            <div className="fuente-dato-tick" onClick={e => this.toggleFuente(e, "scholar")}></div>
                            <div className="fuente-dato-name">Google Scholar</div>
                        </div>
                    </div>
                </div>
                <div className="carga-años">
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
                    <button className="boton-formulario" onClick={() => this.cargar()}>Cargar</button>
                </div>
            </div>
        );
    }
}

export default Carga;
