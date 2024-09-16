function Scroll({ total, current }) {

    var marginLeft = Math.max(0, (0.8 * window.innerWidth - 120) / (total - 1) * current);

    return (
        <div className="scroll-track">
            <div className="scroll-thumb" style={{ marginLeft: `${marginLeft}px` }}></div>
        </div>
    )
}

export default Scroll;