// import { useState } from "react";

// export function getCountries(): string[] {
//   const url = "https://restcountries.com/v3.1/independent?status=true";

//   fetch(url)
//     .then((res) => {
//       const countriesArray: string[] = [];
//       if (!res.ok) {
//         return [];
//       }
//       const jsonCountries: any = res.json();
//       return jsonCountries.then((countries: any) => {
//         countriesArray.push(countries.name.common);
//       });
//       //   jsonCountries.array.forEach((element) => {
//       //     countriesArray.push(element.name.common);
//       //   });
//       //   countriesArray.push(jsonCountries.name.common);
//       return countriesArray;
//     })
//     .catch((error) => {
//       console.error(error);
//       return [];
//     });
//   return [];
// }
