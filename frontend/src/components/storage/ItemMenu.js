import { useEffect, useRef } from "react";


export default function ItemMenu({ isVisible, setVisible, deleteCallback, renameCallback }) {

  const wrapperRef = useRef(null);
  const className = isVisible ? "storage-item-menu active" : "storage-item-menu"

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
        setVisible(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [wrapperRef]);


  return (
    <div ref={wrapperRef}>
      <div className={className}>
        <button className="button menu" onClick={(e) => renameCallback(e)}>Переименовать</button>
        <button className="button menu" onClick={(e) => deleteCallback(e)}>Удалить</button>
      </div>
    </div>
  );
}