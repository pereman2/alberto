const request = require('axios');

export async function requestDBSearch(titulo, autor, fechaI, fechaF, article, congress, book) {
    let publications = 0;
    let requestDB = "http://localhost:8080/ApiRestDB/servicios/BuscarDB/";
    const query = {
        'url': requestDB + titulo + "/" + autor + "/" + fechaI + "/" + fechaF + "/" + article + "/" + congress + "/" + book,
        'rejectUnauthorized': false,
    };
    console.log(query);
    await request(query)
        .then(function (response) {
            {
                publications = response.data;
                console.log(publications["publicaciones"])
            }
        });
    return publications["publicaciones"];
}

export async function requestDBLoad(fechaI, fechaF, dblp, ieeex, scholar) {
    let publications = 0;
    let requestDB = "http://localhost:8080/ApiRestDB/servicios/LoadDB/";
    const query = {
        'url': requestDB + fechaI + "/" + fechaF + "/" + dblp + "/" + ieeex + "/" + scholar,
        'rejectUnauthorized': false,
    };
    console.log(query);
    await request(query)
        .then(function (response) {
            {
                publications = response.data;
                console.log(publications)
            }
        });
    return publications;
}