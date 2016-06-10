interface Kieli{
  arvo: string;
  nimi: any;
  uri: string;
}

interface Kielipari {
  kielesta:Kieli;
  kieleen:Kieli;
}

const kielipariMatch = (a:Kielipari,b:Kielipari):boolean =>(a.kieleen === b.kieleen || a.kieleen === b.kielesta) &&
  (a.kielesta === b.kielesta || a.kielesta === b.kieleen);

export {Kieli, Kielipari, kielipariMatch}