

export default function Modal({ children, visible, setVisible }) {

  const className = visible ? 'modal active' : 'modal'

  const closeModal = () => {
    setVisible(false);
  }

  return (
    <div className={className} onClick={closeModal}>
      <div className='modal-content' onClick={(e) => e.stopPropagation()}>
        {children}
      </div>
    </div>
  );
}