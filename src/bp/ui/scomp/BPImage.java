package bp.ui.scomp;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class BPImage extends JComponent implements ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8289168943695561205L;

	protected Image m_img;
	protected Image m_buffer;
	protected int m_layout;
	protected Float m_scale = null;
	protected int m_x;
	protected int m_y;
	protected int m_w;
	protected int m_h;
	protected int m_sx;
	protected int m_sy;
	protected int m_sw;
	protected int m_sh;

	protected int m_offsetx;
	protected int m_offsety;

	protected Point m_downpt;
	protected Point m_oripos;

	public BPImage()
	{
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	public void resetAndSetImage(Image img)
	{
		m_scale = null;
		m_x = 0;
		m_y = 0;
		m_w = 0;
		m_h = 0;
		m_sx = 0;
		m_sy = 0;
		m_sw = 0;
		m_sh = 0;

		m_offsetx = 0;
		m_offsety = 0;

		m_downpt = null;
		m_oripos = null;
		setImage(img);
	}

	public void setImage(Image img)
	{
		m_img = img;
		calcImage(true);
		repaint();
	}

	public Image getImage()
	{
		return m_img;
	}

	protected void calcImage(boolean onset)
	{
		if (m_img == null)
			return;
		Image img = m_img;
		int w = getWidth();
		int h = getHeight();
		int iow = img.getWidth(null);
		int ioh = img.getHeight(null);
		int iw = iow;
		int ih = ioh;
		boolean needscale = (w < iw || h < ih);
		if (needscale)
		{
			float ws = (float) iw / (float) w;
			float hs = (float) ih / (float) h;
			if (ws > hs)
			{
				iw = w;
				ih = (int) ((float) ih / ws);
			}
			else
			{
				ih = h;
				iw = (int) ((float) iw / hs);
			}
			m_sw = iow;
			m_sh = ioh;
		}
		else
		{
			m_sw = iow;
			m_sh = ioh;
		}
		if (onset)
		{
			m_x = (w - iw) / 2;
			m_y = (h - ih) / 2;
			m_sx = 0;
			m_sy = 0;
		}
		m_w = iw;
		m_h = ih;
	}

	protected void paintComponent(Graphics g)
	{
		if (m_img != null)
		{
			Graphics2D g2d = ((Graphics2D) g);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			AffineTransform f = g2d.getTransform();
			float scale = 1f;
			if (f != null)
			{
				scale = (float) f.getScaleY();
			}
			if (scale != 1f)
			{
				f.setToScale(1, 1);
				g2d.setTransform(f);
				int tx = (int) ((float) (m_x + m_offsetx) * scale);
				int ty = (int) ((float) (m_y + m_offsety) * scale);
				g.drawImage(m_img, tx, ty, m_w + tx, m_h + ty, m_sx, m_sy, m_sw + m_sx, m_sh + m_sy, null);
			}
			else
			{
				g.drawImage(m_img, m_x + m_offsetx, m_y + m_offsety, m_w + m_x + m_offsetx, m_h + m_y + m_offsety, m_sx, m_sy, m_sw + m_sx, m_sh + m_sy, null);
			}
		}
	}

	public void componentResized(ComponentEvent e)
	{
		if (m_img != null)
		{
			if (m_scale == null)
			{
				calcImage(true);
				repaint();
			}
		}
	}

	public void componentMoved(ComponentEvent e)
	{
	}

	public void componentShown(ComponentEvent e)
	{
	}

	public void componentHidden(ComponentEvent e)
	{
	}

	public void zoom(float scale)
	{
		m_scale = scale;
		int iw = Math.round(m_sw * scale);
		int ih = Math.round(m_sh * scale);
		int w = getWidth();
		int h = getHeight();
		m_x = (w - iw) / 2;
		m_y = (h - ih) / 2;
		m_w = iw;
		m_h = ih;
		repaint();
	}

	public void zoomDelta(int se)
	{
		float s;
		s = ((m_scale == null) ? (m_sw / m_w) : m_scale);
		if (se > 0)
		{
			if (s >= 2f)
				s += (se * 0.5);
			else if (s >= 1f)
				s += (se * 0.25);
			else if (s >= 0.5f)
				s += (se * 0.25);
			else if (s >= 0.1f)
				s += (se * 0.1);
			else if (s >= 0.05f)
				s += (se * 0.025);
			else if (s >= 0.01f)
				s += (se * 0.01);
		}
		else if (se < 0)
		{
			if (s > 2f)
				s += (se * 0.5);
			else if (s > 1f)
				s += (se * 0.25);
			else if (s > 0.5f)
				s += (se * 0.25);
			else if (s > 0.1f)
				s += (se * 0.1);
			else if (s > 0.05f)
				s += (se * 0.025);
			else if (s > 0.01f)
				s += (se * 0.01);
			if (s < 0.01f)
				s = 0.01f;
		}
		zoom(s);
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			m_downpt = e.getPoint();
			m_oripos = new Point(m_offsetx, m_offsety);
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		m_downpt = null;
		m_oripos = null;
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
		m_downpt = null;
		m_oripos = null;
	}

	public void mouseDragged(MouseEvent e)
	{
		if (m_downpt != null)
		{
			Point pt = e.getPoint();
			m_offsetx = m_oripos.x + pt.x - m_downpt.x;
			m_offsety = m_oripos.y + pt.y - m_downpt.y;
			repaint();
		}
	}

	public void mouseMoved(MouseEvent e)
	{
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		zoomDelta(-1 * e.getWheelRotation());
	}

	public void pasteImage(Image img)
	{
		int r = JOptionPane.showConfirmDialog(this, "Replace Image", "Confirm", JOptionPane.YES_NO_OPTION);
		if (r == JOptionPane.YES_OPTION)
		{
			setImage(img);
		}
	}
}
