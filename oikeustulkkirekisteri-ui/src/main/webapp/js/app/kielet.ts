class Kieli{
  arvo: string;
  nimi: any;
  uri: string;
}

class Kielipari {
  kielesta:Kieli;
  kieleen:Kieli;

  isMatch(kielipari:Kielipari){
    return (this.kieleen === kielipari.kieleen || this.kieleen === kielipari.kielesta) &&
        (this.kielesta === kielipari.kielesta || this.kielesta === kielipari.kieleen);
  }

  constructor(kielesta:Kieli, kieleen:Kieli) {
    if (kielesta === kieleen) {
      console.error("kielet ovat samat");
      //TODO virheiden näyttäminen käyttäjälle
      throw "kielet ovat samat";
    }

    this.kielesta = kielesta;
    this.kieleen = kieleen;
  }
}

export {Kieli, Kielipari}