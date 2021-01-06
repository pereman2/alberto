import React, { Component } from 'react';
import '../css/ResultadoCarga.css';

class ResultadoCarga extends Component {
    constructor(props) {
        super(props)
        this.state = {
            totals: undefined,
        };
    }

    getTotals() {
        let totals = this.props.totals;
        let res = [];
        let total = 0;
        if (totals !== undefined) {
            if (totals["dblp"] !== undefined) {
                total = totals["dblp"];
                res.push(<span className="rc-total-line">DBLP: {total}</span>);
            }
            if (totals["ieeex"] !== undefined) {
                let ieex = totals["ieeex"] - total;
                total += ieex;
                res.push(<span className="rc-total-line">IEEE XPLORE: {ieex}</span>);
            }
            if (totals["scholar"] !== undefined) {
                let scholar = totals["scholar"] - total;
                res.push(<span className="rc-total-line">GOOGLE SCHOLAR: {scholar}</span>);
            }
        }
        return res;
    }

    getTotal() {
        let res = 0;
        let totals = this.props.totals;
        console.log(totals);
        if (totals !== undefined) {
            if (totals["dblp"] !== undefined) {
                res = totals["dblp"];
            }
            if (totals["ieeex"] !== undefined) {
                res = totals["ieeex"];
            }
            if (totals["scholar"] !== undefined) {
                res = totals["scholar"];
            }
        }
        return "Total de referencias cargadas: " + res;
    }

    render() {
        return (
            <div className="rc-wrapper">
                <div className="title">Resultados de la carga</div>
                <hr className="line"></hr>
                <div className="rc-totals-title">Número de referencias cargadas</div>
                <div className="rc-totals">{this.getTotals()} </div>
                <div className="rc-total">{this.getTotal()}</div>
                <div className="rc-buttons">
                    <button onClick={this.props.onExit} className="boton-formulario" >Ok</button>
                </div>
            </div>
        );
    }
}

export default ResultadoCarga;