import { useState } from "react";
import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";
import "./style/App.css";
import Visual from "./Visual";

function App() {
  const [count, setCount] = useState(0);

  return (
    <div>
      <Visual />
    </div>
  );
}

export default App;
