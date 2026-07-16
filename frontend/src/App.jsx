import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import Home from './components/home/Home';
import Inactive from './components/Inactive/Inactive';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login/>}/>
        <Route path="/register" element={<Register/>}/>
        <Route path="/home" element={<Home/>}/>
        <Route path="/inactive" element={<Inactive/>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
